package com.muxu.datasource

import com.google.common.collect.Lists
import com.muxu.datasource.entity.CatalogTreeDTO
import com.muxu.datasource.entity.CatalogType
import com.muxu.core.exception.BaseException
import org.apache.commons.lang3.StringUtils
import java.sql.*
import java.util.*

interface JdbcConnection {

    /**
     * Get the jdbc driver name
     */
    val driverName: String

    /**
     * 获取到指定dbUrl的数据库连接
     *
     * @param username 数据库连接的用户名
     * @param passwd 数据库连接的密码
     * @param dbUrl 数据库连接url
     * @return 数据库连接实例
     */
    @Throws(Exception::class)
    fun getConnection(username: String?, passwd: String?, dbUrl: String): Connection {
        var driverName = driverName
        if (StringUtils.isEmpty(driverName)) {
            driverName = getDriverName(dbUrl)
        }
        Class.forName(driverName)
        return DriverManager.getConnection(dbUrl, username, passwd)
    }

    /**
     * Get the jdbc driver name based on db url
     *
     * @param dbUrl jdbc db url
     * @return driver name
     */
    fun getDriverName(dbUrl: String): String {
        return ""
    }

    /**
     * build basic jdbc URL
     *
     * @param host host
     * @param port port
     * @param db db
     * @return jdbc url
     */
    fun buildUrl(host: String, port: String, db: String?): String

    fun fetchCatalogs(username: String, password: String, url: String): List<CatalogTreeDTO> {
        var conn: Connection? = null
        return try {
            // 打开链接
            conn = getConnection(username, password, url)
            val metaData = conn.metaData

            // 获取所有db
            val dbs = metaData.catalogs
            val dbList: MutableList<CatalogTreeDTO> = Lists.newArrayListWithCapacity(dbs.fetchSize)
            while (dbs.next()) {
                val dbName = dbs.getString("TABLE_CAT")
                val dbTreeDTO = CatalogTreeDTO(CatalogType.DATABASE.name, dbName, null, null)
                dbTreeDTO.subCatalog = getSchemaCatalog(metaData, dbName)
                dbList.add(dbTreeDTO)
            }
            dbList
        } finally {
            // 关闭资源
            try {
                conn?.close()
            } catch (se: SQLException) {
                throw BaseException()
            }
        }
    }

    fun execute(
        username: String,
        password: String,
        dbUrl: String,
        query: String
    ): List<Map<String, String>> {
        var conn: Connection? = null
        var statement: Statement? = null
        val result: MutableList<Map<String, String>> = ArrayList()
        return try {
            conn = getConnection(username, password, dbUrl)
            statement = conn.createStatement()
            val resultSet = statement.executeQuery(query)
            val rsmd = resultSet.metaData
            val columnCount = rsmd.columnCount
            while (resultSet.next()) {
                val map: MutableMap<String, String> = HashMap()
                // 从1开始计数
                for (i in 1..columnCount) {
                    map[rsmd.getColumnName(i)] = resultSet.getString(i)
                }
                result.add(map)
            }
            result
        } catch (e: Exception) {
            throw e
        } finally {
            // 关闭资源
            statement?.close()
            conn?.close()
        }
    }

    fun getSchemaCatalog(metaData: DatabaseMetaData, dbName: String?): List<CatalogTreeDTO> {
        val schemas = metaData.getSchemas(dbName, null)
        val schemaList: MutableList<CatalogTreeDTO> =
            Lists.newArrayListWithCapacity(schemas.fetchSize)
        while (schemas.next()) {
            val schema: String? = schemas.getString("TABLE_SCHEM")
            val catalog: String? = schemas.getString("TABLE_CATALOG")
            if (dbName.equals(catalog, ignoreCase = true) || Objects.isNull(catalog)) {
                schemaList.add(
                    CatalogTreeDTO(
                        CatalogType.SCHEMA.name, schema, null,
                        getTableCatalog(
                            metaData,
                            catalog,
                            schema
                        )
                    )
                )
            }
        }
        return schemaList
    }

    fun getTableCatalog(metaData: DatabaseMetaData, catalog: String?, schema: String?): List<CatalogTreeDTO> {
        val types = arrayOf("TABLE")
        val tables = metaData.getTables(catalog, schema, null, types)
        val tableList: MutableList<CatalogTreeDTO> = Lists.newArrayListWithCapacity(tables.fetchSize)
        while (tables.next()) {
            val tableName = tables.getString("TABLE_NAME")
            tableList.add(CatalogTreeDTO(CatalogType.TABLE.name, tableName, null, null))
        }
        return tableList
    }

}

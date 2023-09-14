package com.muxu.utils.datasource.impl

import com.google.common.collect.Lists
import com.muxu.utils.datasource.JdbcConnection
import com.muxu.utils.entity.CatalogTreeDTO
import com.muxu.utils.entity.CatalogType
import java.sql.*
import java.util.*
import java.util.stream.Collectors

class PgConnection(
    override val driverName: String = "org.postgresql.Driver"
) : JdbcConnection {

    override fun buildUrl(host: String, port: String, db: String?): String {
        return String.format(
            PG_URL_FORMAT,
            host,
            port,
            db ?: ""
        )
    }

    fun fetchCatalogs(username: String, password: String, url: String): List<CatalogTreeDTO> {
        var conn: Connection? = null
        return try {
            // 打开链接
            conn = getConnection(username, password, url)
            val metaData = conn.metaData

            // 获取所有db
            val dbs = metaData.catalogs
            val dbList: MutableList<CatalogTreeDTO> = Lists.newArrayListWithCapacity<CatalogTreeDTO>(dbs.fetchSize)
            while (dbs.next()) {
                val label = "TABLE_CAT"
                val dbName = dbs.getString(label)
                val dbTreeDTO = CatalogTreeDTO(CatalogType.DATABASE.name, dbName, null, null)

                // 获取所有schema
                val schemas = metaData.schemas
                val schemaList: MutableList<CatalogTreeDTO> =
                    Lists.newArrayListWithCapacity(schemas.fetchSize)
                while (schemas.next()) {
                    val schema: String? =
                        schemas.getString("TABLE_SCHEM")
                    val catalog: String? =
                        schemas.getString("TABLE_CATALOG")
                    // PgDatabaseMetaData.getSchemas时，TABLE_CATALOG会固定返回null;
                    // 因而对PostgreSQL, 若catalog为null, 也要把结果加入返回列表
                    if (dbName.equals(catalog, ignoreCase = true) || Objects.isNull(catalog)) {
                        schemaList.add(
                            CatalogTreeDTO(
                                CatalogType.SCHEMA.name, schema, null,
                                getTablesAsTree(
                                    metaData,
                                    catalog,
                                    schema
                                )
                            )
                        )
                    }
                }
                dbTreeDTO.subCatalog = schemaList
                dbList.add(dbTreeDTO)
            }
            dbList
        } finally {
            // 关闭资源
            try {
                conn?.close()
            } catch (se: SQLException) {
            }
        }
    }

    private fun getTablesAsTree(metaData: DatabaseMetaData, catalog: String?, schema: String?): List<CatalogTreeDTO> {
        val types = arrayOf("TABLE")
        val tables = metaData.getTables(catalog, schema, null, types)
        val tableList: MutableList<CatalogTreeDTO> = Lists.newArrayListWithCapacity(tables.fetchSize)
        while (tables.next()) {
            val tableName = tables.getString("TABLE_NAME")
            tableList.add(CatalogTreeDTO(CatalogType.TABLE.name, tableName, null, null))
        }
        return tableList
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

    companion object {
        const val PG_URL_FORMAT =
            "jdbc:postgresql://%s:%s/%s?rewriteBatchedStatements=true&autoReconnect=true&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull"

        @JvmStatic
        fun main(args: Array<String>) {
            var pgConnection = PgConnection()
            val username = "dts_test_account"
            val password = "4L9Fm#3dnV"
            val url = pgConnection.buildUrl("postgres-d60f9dbab229.volces.com", "5432", "dts_test")
            val collect = pgConnection.execute(
                username, password, url, """
                    select datname       as name,
                           datistemplate as is_template,
                           datallowconn  as allow_connections
                    from pg_database N
                    where datistemplate = false and datallowconn = true
            """.trimIndent()
            ).stream()
                .map { result -> result["name"] }
                .map { databaseName ->
                    pgConnection.fetchCatalogs(
                        username,
                        password,
                        pgConnection.buildUrl("postgres-d60f9dbab229.volces.com", "5432", databaseName)
                    )
                }
                .filter(Objects::nonNull)
                .collect(Collectors.toList())

            println(collect)
        }
    }
}
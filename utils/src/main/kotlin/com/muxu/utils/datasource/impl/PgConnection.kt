package com.muxu.utils.datasource.impl

import com.google.common.collect.Lists
import com.muxu.utils.datasource.JdbcConnection
import com.muxu.utils.entity.CatalogTreeDTO
import com.muxu.utils.entity.CatalogType
import com.muxu.utils.entity.DataSource
import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.SQLException
import java.util.*

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

    fun fetchCatalogs(datasource: DataSource): List<CatalogTreeDTO> {
        val username = "dts_test_account"
        val password = "4L9Fm#3dnV"
        val url = buildUrl("postgres-d60f9dbab229.volces.com", "5432", null)
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

    companion object {
        const val PG_URL_FORMAT =
            "jdbc:postgresql://%s:%s/%s?rewriteBatchedStatements=true&autoReconnect=true&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull"

        @JvmStatic
        fun main(args: Array<String>) {
            var fetchCatalogs = PgConnection().fetchCatalogs(DataSource())
            println(fetchCatalogs)
        }
    }
}
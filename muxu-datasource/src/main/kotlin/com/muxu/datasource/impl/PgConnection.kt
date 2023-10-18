package com.muxu.datasource.impl

import com.muxu.datasource.JdbcConnection

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

    companion object {
        const val PG_URL_FORMAT =
            "jdbc:postgresql://%s:%s/%s?rewriteBatchedStatements=true&autoReconnect=true&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull"

    }
}
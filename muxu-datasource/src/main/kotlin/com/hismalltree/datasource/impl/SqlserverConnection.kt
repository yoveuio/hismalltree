package com.hismalltree.datasource.impl;

import com.hismalltree.datasource.JdbcConnection

/**
 * @author fangpeiyu.py
 */
class SqlserverConnection(
    override val driverName: String = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
) : JdbcConnection {
    override fun buildUrl(host: String, port: String, db: String?): String {
        return String.format(
            SQLSERVER_URL_FORMAT,
            host,
            port,
            db ?: ""
        )
    }

    companion object {

        const val SQLSERVER_URL_FORMAT =
            "jdbc:sqlserver://%s:%s;databaseName=%s;" +
                    "trustServerCertificate=true;connectRetryCount=2;connectRetryInterval=1;loginTimeout=2;socketTimeout=5000"

    }
}

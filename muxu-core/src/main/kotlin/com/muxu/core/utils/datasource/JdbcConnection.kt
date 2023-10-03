package com.muxu.core.utils.datasource

import org.apache.commons.lang3.StringUtils
import java.sql.Connection
import java.sql.DriverManager

interface JdbcConnection {

    /**
     * Get the jdbc driver name
     */
    val driverName: String?

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

}

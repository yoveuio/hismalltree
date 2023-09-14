package com.muxu.utils.entity

import org.apache.commons.lang3.StringUtils

/**
 * @author feike
 * @Date 2023/7/13
 */
enum class CatalogType {
    /**
     * datasource
     */
    DATASOURCE,

    /**
     * database
     */
    DATABASE,

    /**
     * schema
     */
    SCHEMA,

    /**
     * table
     */
    TABLE;

    companion object {
        fun ofNameIgnoreCase(name: String?): CatalogType {
            return valueOf(StringUtils.upperCase(name))
        }
    }
}

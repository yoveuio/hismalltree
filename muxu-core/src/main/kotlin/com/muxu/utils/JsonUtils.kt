package com.muxu.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import org.apache.commons.lang3.StringUtils

/**
 * @author fangpeiyu.py
 */
object JsonUtils {

    private val objectMapper: ObjectMapper = ObjectMapper()

    @JvmStatic
    fun <T> convertValue(value: Any?, typeReference: TypeReference<T>): T = objectMapper.convertValue(value, typeReference)

    @JvmStatic
    fun fromJson(value: String?): JsonNode {
        if (StringUtils.isEmpty(value)) {
            return JsonNodeFactory.instance.nullNode()
        }
        return objectMapper.readTree(value)
    }

    @JvmStatic
    fun <T> fromJson(value: String?, clazz: Class<T>): T? {
        if (StringUtils.isEmpty(value)) {
            return null
        }
        return objectMapper.readValue(value, clazz)
    }

    @JvmStatic
    fun toJson(value: Any?): String? {
        if (value == null) {
            return "";
        }
        return objectMapper.writeValueAsString(value)
    }
}

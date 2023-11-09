package com.hismalltree.core.utils

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.json.JsonReadFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.hismalltree.core.constants.TypeReferenceHolder
import org.apache.commons.lang3.StringUtils

/**
 * @author fangpeiyu.py
 */
object JsonUtils {

    private val objectMapper: ObjectMapper

    init {
        objectMapper = JsonMapper.builder()
            .addModule(JavaTimeModule())
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
            .configure(
                JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(),
                true
            )
            .configure(
                JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(),
                true
            )
            .configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false
            )
            .configure(
                MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES,
                true
            ).configure(
                MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS,
                true
            ).configure(
                MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES,
                true
            )
            .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
            .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            .build()

    }

    @JvmStatic
    fun <T> convertValue(value: Any?, typeReference: TypeReferenceHolder<T>): T =
        objectMapper.convertValue(value, typeReference.typeReference)


    fun convertValueToMap(value: Any?): Map<String, Any> {
        return convertValue(value, TypeReferenceHolder.STRING_OBJECT_MAP_TYPE)
    }


    @JvmStatic
    fun <T> convertValue(value: Any?, clazz: Class<T>): T = objectMapper.convertValue(value, clazz)

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

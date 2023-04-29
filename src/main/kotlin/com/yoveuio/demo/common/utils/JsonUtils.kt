package com.yoveuio.demo.common.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

@Suppress("unused")
object JsonUtils {

    private val mapper: ObjectMapper = ObjectMapper()

    fun toJson(data: Any): String = mapper.writeValueAsString(data)

    fun fromJson(jsonString: String): JsonNode = mapper.readTree(jsonString)
    fun <T> fromJson(jsonString: String, clazz: Class<T>): T = mapper.readValue(jsonString, clazz)
    fun <T> fromJson(jsonString: String, typeReference: TypeReference<T>): T = mapper.readValue(jsonString, typeReference)
}

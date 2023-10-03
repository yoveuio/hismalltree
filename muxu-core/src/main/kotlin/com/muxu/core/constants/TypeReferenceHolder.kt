package com.muxu.core.constants

import com.fasterxml.jackson.core.type.TypeReference

open class TypeReferenceHolder<T>(
    val typeReference: TypeReference<T>
) {

    @Suppress("unused")
    companion object {

        val STRING_LIST_TYPE by lazy { TypeReferenceHolder(object : TypeReference<List<String>>() {}) }
        val INT_LIST_TYPE by lazy { TypeReferenceHolder(object : TypeReference<List<Int>>() {}) }
        val STRING_MAP_TYPE by lazy { TypeReferenceHolder(object : TypeReference<Map<String, String>>() {}) }
        val STRING_OBJECT_MAP_TYPE by lazy { TypeReferenceHolder(object : TypeReference<Map<String, Any>>() {}) }

    }

}
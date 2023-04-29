package com.yoveuio.demo.common

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class PageResult(
    val total: Int,
    val pageNum: Int,
    val pageSize: Int,
    val data: Any
)

class SuccessResponse(
    val code: Int,
    val message: String,
    val data: Any?,
    val page: PageInfo?
) {
    constructor(data: Any?) : this(0, "success", data, null)
    constructor(pageResult: PageResult) : this(
        0,
        "success",
        pageResult.data,
        PageInfo(pageResult.total, pageResult.pageNum, pageResult.pageSize)
    )

    class PageInfo(
        val total: Int,
        @field: JsonProperty("page_num") val pageNum: Int,
        @field: JsonProperty("page_size") val pageSize: Int
    )
}

class FailedResponse(data: Any?, status: HttpStatus) : ResponseEntity<Any>(data, null, status)

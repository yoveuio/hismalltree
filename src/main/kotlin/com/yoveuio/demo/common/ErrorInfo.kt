package com.yoveuio.demo.common

sealed interface ErrorInfo {

    fun errorType(): ErrorType

    fun message(): String

}

sealed class CustomException(override val message: String?): Exception(message) {
    constructor(errorInfo: ErrorInfo, data: Any): this(errorInfo.message())
}

enum class ErrorType {

    TIMEOUT

}

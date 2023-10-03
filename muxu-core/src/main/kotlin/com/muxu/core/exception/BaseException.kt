package com.muxu.core.exception

open class BaseException(
    message: String?,
    cause: Throwable?
) :
    RuntimeException(message, cause) {
    constructor() : this(null, null)
    constructor(cause: Throwable) : this(cause.message, cause)
    constructor(message: String) : this(message, null)
    constructor(message: ExceptionMessage) : this(message.name(), null)

}
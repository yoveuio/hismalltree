package com.muxu.core.exception;

/**
 * @author fangpeiyu.py
 */
class ExternalException(message: String?, cause: Throwable?) : BaseException(message, cause) {

    constructor(message: ExceptionMessage) : this(message.name(), null)

}

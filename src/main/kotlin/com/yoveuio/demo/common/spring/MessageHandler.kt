package com.yoveuio.demo.common.spring

import jakarta.annotation.Resource
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component

@Component
class MessageHandler() {

    @Resource
    private lateinit var messageSource: MessageSource

    private val logger = LoggerFactory.getLogger(MessageHandler::class.java)

}
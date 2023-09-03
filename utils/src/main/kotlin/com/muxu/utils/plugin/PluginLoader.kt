package com.muxu.utils.plugin

import java.util.logging.Logger

class PluginLoader<T>(val clazz: Class<T>, val classLoader: ClassLoader = Thread.currentThread().contextClassLoader) {



    companion object {

        private val LOG: Logger = Logger.getLogger(Companion::class.java.name)

    }

}
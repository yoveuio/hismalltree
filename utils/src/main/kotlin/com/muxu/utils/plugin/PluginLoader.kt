package com.muxu.utils.plugin

import jdk.incubator.vector.VectorOperators.LOG
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class PluginLoader<T>(val clazz: Class<T>, val classLoader: ClassLoader = Thread.currentThread().contextClassLoader) {

    private val components = HashMap<String, T>(16)

    private val loaded = AtomicBoolean(false)

    fun loadComponent() {
    }

    @Synchronized
    private fun load() {
        val loadedServices = ServiceLoader.load(clazz, classLoader)
        loadedServices.forEach { loadedService ->
            if (loadedService !is Component) {
                logger.warn("This service is not a component: {}", loadedService)
                return@forEach
            }
            components[(loadedService as Component).name()] = loadedService
        }
    }

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(Companion::class.java.name)

    }

}
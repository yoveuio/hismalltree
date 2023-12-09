package com.muxu.demo.spring.config

import com.hismalltree.core.annotation.Slf4j
import com.hismalltree.core.config.CustomConfig
import com.typesafe.config.Config
import com.typesafe.config.ConfigList
import com.typesafe.config.ConfigObject
import com.typesafe.config.ConfigValue
import org.springframework.boot.env.PropertySourceLoader
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.PropertySource
import org.springframework.core.io.Resource

@Slf4j
class CustomPropertySourceLoader: PropertySourceLoader {
    override fun getFileExtensions(): Array<String> {
        return arrayOf("conf", "yaml", "properties")
    }

    override fun load(name: String?, resource: Resource?): MutableList<PropertySource<*>> {
        val propertySources: MutableList<PropertySource<*>> = ArrayList()
        val config = CustomConfig.config
        val properties = LinkedHashMap<String, Any>()
        toFlatMap(properties, config)
        propertySources.add(MapPropertySource(name!!, properties))
        return propertySources
    }

    private fun toFlatMap(properties: LinkedHashMap<String, Any>, config: Config) {
        toFlatMap(properties, "", config)
    }

    private fun toFlatMap(properties: MutableMap<String, Any>, key: String, config: Config) {
        val prefix = if ("" == key) "" else "$key."
        for ((key1, value) in config.entrySet()) {
            val propertyKey = prefix + key1
            addConfigValue(properties, propertyKey, value)
        }
    }

    private fun addConfigValue(properties: MutableMap<String, Any>, key: String, value: ConfigValue) {
        when (value) {
            is ConfigList -> {
                processListValues(properties, key, value)
            }

            is ConfigObject -> {
                processObjectValues(properties, key, value)
            }

            else -> {
                addScalarValue(properties, key, value)
            }
        }
    }

    private fun processListValues(properties: MutableMap<String, Any>, key: String, values: ConfigList) {
        for ((i, value) in values.withIndex()) {
            addConfigValue(properties, String.format("%s[%d]", key, i), value)
        }
    }

    private fun processObjectValues(properties: MutableMap<String, Any>, key: String, value: ConfigObject) {
        toFlatMap(properties, key, value.toConfig())
    }

    private fun addScalarValue(properties: MutableMap<String, Any>, key: String, value: ConfigValue) {
        properties[key] = value.unwrapped()
    }
}
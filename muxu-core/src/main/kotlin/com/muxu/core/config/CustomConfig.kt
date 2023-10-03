package com.muxu.core.config

import com.google.common.base.CaseFormat
import com.muxu.core.constants.EnvConstants.Companion.APP_PATH_ENV
import com.muxu.core.constants.EnvConstants.Companion.DEFAULT_APP_PATH
import com.muxu.core.constants.TypeReferenceHolder
import com.muxu.core.utils.FilenameUtils
import com.muxu.core.utils.JsonUtils
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import java.util.*
import java.util.stream.Collectors

class CustomConfig {

    companion object {

        private var config: Config

        init {
            val configPath = System.getenv().getOrDefault(APP_PATH_ENV, DEFAULT_APP_PATH)

            Companion::class.java.classLoader.getResourceAsStream(configPath).use {
                config = if (Objects.equals(FilenameUtils.getExtension(configPath), "yaml")) {
                    val yaml = Yaml(LoaderOptions())
                    val o: Map<String, Any> = yaml.load(it)
                    ConfigFactory.parseMap(looseBinding(o))
                } else {
                    ConfigFactory.load()
                }
            }
        }

        fun getOrDefault(key: String, defaultValue: String): String {
            return if (config.hasPath(key)) config.getString(key) else defaultValue
        }

        fun <T> get(key: String, clazz: Class<T>): T? {
            if (!config.hasPath(key)) {
                return null
            }
            return JsonUtils.convertValue(config.getAnyRef(key), clazz)
        }

        fun <T> get(key: String, type: TypeReferenceHolder<T>): T? {
            if (!config.hasPath(key)) {
                return null
            }
            val configStr = config.getValue(key).render()
            return JsonUtils.convertValue(configStr, type)
        }

        private fun looseBinding(map: Map<String, Any>): Map<String, Any> {
            fun getLooseKey(originKey: Any?): Any? {
                if (originKey !is String) {
                    return originKey
                }
                for (value in LooseFormatMapping.values()) {
                    if (originKey.contains(value.identifier)) {
                        return value.caseFormat.to(CaseFormat.LOWER_CAMEL, originKey)
                    }
                }
                return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_CAMEL, originKey)
            }

            fun dfs(any: Any?): Any? {
                return when (any) {
                    is Map<*, *> -> {
                        any.entries.stream()
                            .map { Pair(getLooseKey(it.key), dfs(it.value)) }
                            .collect(Collectors.toMap({ it.first }, { it.second }))
                    }

                    is List<*> -> {
                        any.stream().map { dfs(it) }.toList()
                    }

                    else -> {
                        any;
                    }
                }
            }

            @Suppress("UNCHECKED_CAST")
            return dfs(map) as MutableMap<String, out Any>
        }
    }

}

private enum class LooseFormatMapping(
    val identifier: String,
    val caseFormat: CaseFormat
) {

    LOWER_HYPHEN("-", CaseFormat.LOWER_HYPHEN),
    LOWER_UNDERSCORE("-", CaseFormat.LOWER_UNDERSCORE);

}

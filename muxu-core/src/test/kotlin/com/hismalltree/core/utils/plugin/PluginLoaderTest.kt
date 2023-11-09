package com.hismalltree.core.utils.plugin

import org.junit.Test

class PluginLoaderTest {

    @Test
    fun testLoadPlugin() {
        val pluginLoader = PluginLoader(clazz = TestPlugin::class.java)
    }

}
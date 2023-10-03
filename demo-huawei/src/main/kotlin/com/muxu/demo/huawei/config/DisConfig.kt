package com.muxu.demo.huawei.config

import com.muxu.core.config.CustomConfig
import com.muxu.demo.huawei.constant.HuaweiEnvConstant.Companion.DIS_CONFIG_PATH

open class DisConfig {

    var endpoint: String? = null
    var region: String? = null
    var ak: String? = null
    var sk: String? = null
    var projectId: String? = null
    var streamChannel: Map<StreamChannelEnum, String>? = null


    companion object {

        val instance: DisConfig = CustomConfig.get(DIS_CONFIG_PATH, DisConfig::class.java)!!

    }

}

enum class StreamChannelEnum {

    /**
     * the default stream channel
     */
    DEFAULT

}

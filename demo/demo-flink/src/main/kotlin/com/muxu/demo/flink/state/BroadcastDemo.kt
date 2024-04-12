package com.muxu.demo.flink.state

import org.apache.flink.api.common.state.MapStateDescriptor
import org.apache.flink.api.common.typeinfo.BasicTypeInfo
import org.apache.flink.api.common.typeinfo.TypeHint
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.datastream.BroadcastStream
import org.apache.flink.streaming.api.datastream.DataStream


class BroadcastDemo {



}

data class Item(
    val name: String,
    val color: String
)

data class Rule(
    val name: String
)

fun main() {
    val itemStream: DataStream<Item> = TODO()
    val ruleStream: DataStream<Rule> = TODO()
    val colorPartitionedStream = itemStream.keyBy { item -> item.color }

    // 一个 map descriptor，它描述了用于存储规则名称与规则本身的 map 存储结构
    val ruleStateDescriptor: MapStateDescriptor<String, Rule?> = MapStateDescriptor(
        "RulesBroadcastState",
        BasicTypeInfo.STRING_TYPE_INFO,
        TypeInformation.of(object : TypeHint<Rule?>() {})
    )


// 广播流，广播规则并且创建 broadcast state
    val ruleBroadcastStream: BroadcastStream<Rule> = ruleStream
        .broadcast(ruleStateDescriptor)
}

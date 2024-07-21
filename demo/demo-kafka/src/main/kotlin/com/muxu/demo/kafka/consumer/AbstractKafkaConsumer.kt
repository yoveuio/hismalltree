package com.muxu.demo.kafka.consumer

import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.*


abstract class AbstractKafkaConsumer(
    props: Properties
) : KafkaConsumer<String, String>(props) {
    

}

fun main(args: Array<String>) {


}

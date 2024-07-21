package com.muxu.demo.kafka.produce

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import java.util.*
import java.util.concurrent.Future

abstract class AbstractKafkaProducer(props: Properties): KafkaProducer<Array<Byte>, Array<Byte>>(props) {


    override fun send(record: ProducerRecord<Array<Byte>, Array<Byte>>?): Future<RecordMetadata> {
        return super.send(record)
    }


}
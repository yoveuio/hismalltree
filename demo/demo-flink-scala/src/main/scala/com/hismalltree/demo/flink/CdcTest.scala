package com.hismalltree.demo.flink

import com.ververica.cdc.connectors.mysql.source.MySqlSource
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

object CdcTest {

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration())

    // generate watermarks per 5000ms
    env.getConfig.setAutoWatermarkInterval(5000L)

    val mysqlSource = MySqlSource.builder[String]()
      .hostname("localhost")
      .port(3306)
      .databaseList("app_db")
      .tableList(".*")
      .username("root")
      .password("123456")
      .deserializer(new JsonDebeziumDeserializationSchema())
      .build()

    env.fromSource(mysqlSource, WatermarkStrategy.noWatermarks(), "Mysql Source")

  }

}

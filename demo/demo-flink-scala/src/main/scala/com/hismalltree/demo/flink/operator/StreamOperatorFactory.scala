package com.hismalltree.demo.flink.operator

import com.hismalltree.demo.flink.{CustomSource, FlinkExecutionEnvironment}
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.connector.source.Source
import org.apache.flink.streaming.api.datastream.DataStreamSource

object StreamOperatorFactory {

//  def addSource[OUT]
//  (
//    environment: FlinkExecutionEnvironment,
//    source: CustomSource[OUT, ],
//    watermark: WatermarkStrategy[OUT],
//    sourceName: String
//  ): DataStreamSource[OUT] = {
//    val executionEnvironment = environment.streamExecutionEnvironment
//    new DataStreamSource[OUT](executionEnvironment, source, watermark, getTypeInfo(), sourceName)
//  }

  private def getTypeInfo[OUT](): TypeInformation[OUT] = {
    ???
  }

}

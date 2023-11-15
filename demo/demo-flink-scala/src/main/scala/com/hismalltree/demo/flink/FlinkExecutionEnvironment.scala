package com.hismalltree.demo.flink

import com.hismalltree.core.config.CustomConfig
import com.hismalltree.demo.flink.reader.FlinkReaderDAGBuilder
import com.hismalltree.demo.flink.writer.FlinkWriterDAGBuilder
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

import scala.collection.mutable.ListBuffer

class FlinkExecutionEnvironment
(
  val configuration: CustomConfig
) {

  lazy val streamExecutionEnvironment: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment


  def run
  (
    readerBuilders: List[FlinkReaderDAGBuilder],
    writerBuilders: List[FlinkWriterDAGBuilder]
  ): Unit = {

  }

  private def buildDAG
  (
    readerBuilders: List[FlinkReaderDAGBuilder],
    writerBuilders: List[FlinkWriterDAGBuilder]
  ): Unit = {
    val sources: ListBuffer[DataStream[_]] = ListBuffer[DataStream[_]]()
    for (reader <- readerBuilders) {
      reader.addSource(this, 1)
    }
  }

}

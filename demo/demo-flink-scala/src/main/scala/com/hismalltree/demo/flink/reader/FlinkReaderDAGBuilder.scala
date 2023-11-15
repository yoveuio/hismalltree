package com.hismalltree.demo.flink.reader

import com.hismalltree.core.config.CustomConfig
import com.hismalltree.demo.flink.FlinkExecutionEnvironment

trait FlinkReaderDAGBuilder {

  val validate: Boolean = true

  def configure(environment: FlinkExecutionEnvironment, configuration: CustomConfig): Unit

  def addSource(environment: FlinkExecutionEnvironment, readerParallelism: Int): Unit

}

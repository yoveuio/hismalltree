package com.hismalltree.demo.flink.writer

import com.hismalltree.core.config.CustomConfig
import com.hismalltree.demo.flink.FlinkExecutionEnvironment

trait FlinkWriterDAGBuilder {

  val validate: Boolean = true

  def configure(flinkExecutionEnvironment: FlinkExecutionEnvironment, configuration: CustomConfig): Unit

}

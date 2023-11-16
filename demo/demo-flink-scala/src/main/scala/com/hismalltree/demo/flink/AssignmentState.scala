package com.hismalltree.demo.flink

import javax.annotation.Nullable

trait AssignmentState {

  @Nullable
  var serializedCache: Array[Byte]

  def getType: Int

}

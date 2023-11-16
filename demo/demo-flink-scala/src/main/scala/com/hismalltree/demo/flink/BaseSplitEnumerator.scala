package com.hismalltree.demo.flink

import com.hismalltree.demo.flink.source.{BaseSourceSplit, Checkpoint}
import org.apache.flink.api.connector.source.SplitEnumerator

import java.util

class BaseSplitEnumerator[SplitT <: BaseSourceSplit, CP <: Checkpoint] extends SplitEnumerator[SplitT, CP] {

  override def addReader(subtaskId: Int): Unit = {

  }

  override def start(): Unit = {

  }

  override def handleSplitRequest(subtaskId: Int, requesterHostname: String): Unit = {

  }

  override def addSplitsBack(splits: util.List[SplitT], subtaskId: Int): Unit = {

  }

  override def snapshotState(checkpointId: Long): CP = {
    new Checkpoint
  }

  override def close(): Unit = {

  }
}

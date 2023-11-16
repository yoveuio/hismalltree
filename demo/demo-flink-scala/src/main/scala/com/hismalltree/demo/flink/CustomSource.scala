package com.hismalltree.demo.flink

import org.apache.flink.api.connector.source.{Source, SourceSplit}

class CustomSource[OUT, SplitT <: SourceSplit, StateT] extends Source[OUT, SplitT, StateT] {

}

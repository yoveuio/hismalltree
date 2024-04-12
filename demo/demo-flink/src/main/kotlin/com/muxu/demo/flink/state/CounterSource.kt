package com.muxu.demo.flink.state

import org.apache.flink.api.common.state.ListState
import org.apache.flink.api.connector.source.*
import org.apache.flink.core.io.SimpleVersionedSerializer


class CounterSource : Source<Long, CounterSplit, CounterCheckPointEnum> {

    /** 存储 state 的变量. */
    private val state: ListState<Long>? = null

    /** current offset for exactly once semantics */
    private val offset = 0L

    override fun createReader(readerContext: SourceReaderContext?): SourceReader<Long, CounterSplit> {
        TODO("Not yet implemented")
    }

    override fun getBoundedness(): Boundedness {
        TODO("Not yet implemented")
    }

    override fun getSplitSerializer(): SimpleVersionedSerializer<CounterSplit> {
        TODO("Not yet implemented")
    }

    override fun getEnumeratorCheckpointSerializer(): SimpleVersionedSerializer<CounterCheckPointEnum> {
        TODO("Not yet implemented")
    }

    override fun restoreEnumerator(
        enumContext: SplitEnumeratorContext<CounterSplit>?,
        checkpoint: CounterCheckPointEnum?
    ): SplitEnumerator<CounterSplit, CounterCheckPointEnum> {
        TODO("Not yet implemented")
    }

    override fun createEnumerator(enumContext: SplitEnumeratorContext<CounterSplit>?): SplitEnumerator<CounterSplit, CounterCheckPointEnum> {
        TODO("Not yet implemented")
    }
}

class CounterSplit(private val splitId: String) : SourceSplit {
    override fun splitId(): String = splitId

}

class CounterCheckPointEnum {

}

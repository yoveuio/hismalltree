package com.hismalltree.connector.rocketmq.source.reader

import com.hismalltree.connector.rocketmq.source.RocketMQSourceSplit
import com.hismalltree.connector.rocketmq.source.split.RocketMQSourceSplitState
import com.hismalltree.connector.rocketmq.util.RocketMqUtils
import com.hismalltree.core.annotation.Slf4j
import com.hismalltree.core.annotation.Slf4j.Companion.log
import org.apache.flink.api.connector.source.SourceReaderContext
import org.apache.flink.configuration.Configuration
import org.apache.flink.connector.base.source.reader.SingleThreadMultiplexSourceReaderBase
import org.apache.rocketmq.common.message.MessageQueue
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * The SourceReader requests Splits and processes them.
 * The SourceReaders run in parallel on the Task Managers in the SourceOperators and produce the parallel stream of events/records.
 */
@Slf4j
class RocketMqSourceReader<T>(
    private val rocketMqSourceFetcherManager: RocketMqSourceFetcherManager,
    recordEmitter: RocketMqRecordEmitter<T>,
    configuration: Configuration,
    context: SourceReaderContext,
    private val offsetsToCommit: SortedMap<Long, MutableMap<MessageQueue, Long>> =
        Collections.synchronizedSortedMap(TreeMap()),
    private val offsetsOfFinishedSplits: ConcurrentMap<MessageQueue, Long> = ConcurrentHashMap()
) : SingleThreadMultiplexSourceReaderBase<MessageView, T, RocketMQSourceSplit, RocketMQSourceSplitState>(
    rocketMqSourceFetcherManager,
    recordEmitter,
    configuration,
    context
) {

    override fun onSplitFinished(finishedSplitIds: MutableMap<String, RocketMQSourceSplitState>?) {
        finishedSplitIds?.forEach { (_, splitState) ->
            if (splitState.currentOffset!! >= 0) {
                offsetsOfFinishedSplits[splitState.getMessageQueue()] = splitState.currentOffset
            }
        }
    }

    override fun snapshotState(checkpointId: Long): MutableList<RocketMQSourceSplit> {
        val splits = super.snapshotState(checkpointId)
        if (splits.isEmpty() && offsetsOfFinishedSplits.isEmpty()) {
            offsetsToCommit[checkpointId] = Collections.emptyMap()
        } else {
            val offsetMap = offsetsToCommit.computeIfAbsent(checkpointId) { _ -> HashMap() }
            for (split in splits) {
                if (split.startingOffset >= 0) {
                    offsetMap[RocketMqUtils.getMessageQueue(split)] = split.startingOffset
                }
            }
            offsetMap.putAll(offsetsOfFinishedSplits)
        }
        return splits
    }

    override fun notifyCheckpointComplete(checkpointId: Long) {
        log.debug("Committing offsets for checkpoint $checkpointId")
        val committedPartitions = offsetsToCommit[checkpointId]
        if (committedPartitions == null) {
            log.info("Offsets for checkout $checkpointId either do not exist or have already been commited.")
            return
        }
        rocketMqSourceFetcherManager.commitOffsets(committedPartitions)
    }

    override fun initializedState(split: RocketMQSourceSplit?): RocketMQSourceSplitState {
        return RocketMQSourceSplitState(split!!)
    }

    override fun toSplitType(splitId: String?, splitState: RocketMQSourceSplitState?): RocketMQSourceSplit {
        return splitState!!.getSourceSplit()
    }


}
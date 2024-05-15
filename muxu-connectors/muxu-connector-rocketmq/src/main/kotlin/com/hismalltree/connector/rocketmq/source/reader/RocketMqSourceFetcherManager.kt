package com.hismalltree.connector.rocketmq.source.reader

import com.hismalltree.connector.rocketmq.source.RocketMQSourceSplit
import com.hismalltree.core.annotation.Slf4j
import com.hismalltree.core.annotation.Slf4j.Companion.log
import org.apache.flink.configuration.Configuration
import org.apache.flink.connector.base.source.reader.fetcher.SingleThreadFetcherManager
import org.apache.flink.connector.base.source.reader.fetcher.SplitFetcher
import org.apache.flink.connector.base.source.reader.fetcher.SplitFetcherTask
import org.apache.flink.connector.base.source.reader.splitreader.SplitReader
import org.apache.rocketmq.common.message.MessageQueue
import java.util.function.Supplier

@Slf4j
class RocketMqSourceFetcherManager(
    splitReaderSupplier: Supplier<SplitReader<MessageView, RocketMQSourceSplit>>,
    configuration: Configuration,
) : SingleThreadFetcherManager<MessageView, RocketMQSourceSplit>(
    splitReaderSupplier,
    configuration
) {

    fun commitOffsets(offsetsToCommit: Map<MessageQueue, Long>) {
        if (offsetsToCommit.isEmpty()) {
            return
        }

        log.info("Consumer commit offsets $offsetsToCommit")
        var splitFetcher = fetchers[0]
        if (splitFetcher != null) {
            // The fetcher thread is still running. This should be the majority of the cases.
            enqueueOffsetsCommitTask(splitFetcher, offsetsToCommit)
        } else {
            splitFetcher = createSplitFetcher()
            enqueueOffsetsCommitTask(splitFetcher, offsetsToCommit)
            startFetcher(splitFetcher)
        }
    }

    private fun enqueueOffsetsCommitTask(
        splitFetcher: SplitFetcher<MessageView, RocketMQSourceSplit>,
        offsetsToCommit: Map<MessageQueue, Long>
    ) {
        val splitReader: RocketMqSplitReader = splitFetcher.splitReader as RocketMqSplitReader

        splitFetcher.enqueueTask(
            object : SplitFetcherTask {
                override fun run(): Boolean {
                    splitReader.notifyCheckpointComplete(offsetsToCommit)
                    return true
                }

                override fun wakeUp() {
                    // do nothing
                }
            })
    }

}
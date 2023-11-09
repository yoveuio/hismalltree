package com.muxu.demo.huawei.dis

import com.huaweicloud.dis.DIS
import com.huaweicloud.dis.DISClientBuilder
import com.huaweicloud.dis.exception.DISClientException
import com.huaweicloud.dis.iface.data.request.GetPartitionCursorRequest
import com.huaweicloud.dis.iface.data.request.GetRecordsRequest
import com.huaweicloud.dis.iface.data.response.GetPartitionCursorResult
import com.huaweicloud.dis.iface.data.response.GetRecordsResult
import com.huaweicloud.dis.iface.data.response.Record
import com.huaweicloud.dis.util.PartitionCursorTypeEnum
import com.hismalltree.core.annotation.Slf4j
import com.hismalltree.core.annotation.Slf4j.Companion.log
import com.hismalltree.core.exception.BaseException
import com.muxu.demo.huawei.config.DisConfig
import com.muxu.demo.huawei.config.StreamChannelEnum

@Slf4j
class Consumer(streamChannelEnum: StreamChannelEnum, private val appName: String = "muxu") {

    private val dic: DIS
    private val streamName: String

    init {
        val disConfig = DisConfig.instance
        this.dic = DISClientBuilder.standard()
            .withEndpoint(disConfig.endpoint)
            .withRegion(disConfig.region)
            .withAk(disConfig.ak)
            .withSk(disConfig.sk)
            .withProjectId(disConfig.projectId)
            .withDefaultClientCertAuthEnabled(true)
            .build()

        this.streamName = disConfig.streamChannel?.get(streamChannelEnum) ?: throw BaseException("")
    }

    fun execute(
        partitionId: String = "0",
        startingSequenceNumber: String = "0",
        cursorType: PartitionCursorTypeEnum = PartitionCursorTypeEnum.AT_SEQUENCE_NUMBER
    ) {
        try {
            // 获取数据游标
            val request = GetPartitionCursorRequest()
            request.streamName = this.streamName
            request.partitionId = partitionId
            request.cursorType = cursorType.name
            request.startingSequenceNumber = startingSequenceNumber
            val response: GetPartitionCursorResult = this.dic.getPartitionCursor(request)
            var cursor = response.partitionCursor
            log.info("Get stream {}[partitionId={}] cursor success : {}", this.streamName, partitionId, cursor)

            val recordsRequest = GetRecordsRequest()
            var recordResponse: GetRecordsResult?
            while (true) {
                recordsRequest.partitionCursor = cursor
                recordsRequest.appName = this.appName
                recordResponse = this.dic.getRecords(recordsRequest)
                // 下一批数据游标
                cursor = recordResponse.nextPartitionCursor
                for (record in recordResponse.records) {
                    handleRecord(record)
                }
            }
        } catch (e: DISClientException) {
            log.error(
                "Failed to get a normal response, please check params and retry. Error message [{}]",
                e.message,
                e
            )
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }

    protected fun handleRecord(record: Record) {
        log.info(
            "Get Record [{}], partitionKey [{}], sequenceNumber [{}].",
            java.lang.String(record.data.array()),
            record.partitionKey,
            record.sequenceNumber
        )
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            Consumer(StreamChannelEnum.DEFAULT).execute()
        }
    }

}
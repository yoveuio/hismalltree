package com.muxu.demo.huawei.dis

import com.huaweicloud.dis.DIS
import com.huaweicloud.dis.DISClientBuilder
import com.huaweicloud.dis.core.util.StringUtils
import com.huaweicloud.dis.exception.DISClientException
import com.huaweicloud.dis.iface.data.request.PutRecordsRequest
import com.huaweicloud.dis.iface.data.request.PutRecordsRequestEntry
import com.huaweicloud.dis.iface.data.response.PutRecordsResult
import com.muxu.core.annotation.Slf4j
import com.muxu.core.annotation.Slf4j.Companion.log
import com.muxu.core.exception.BaseException
import com.muxu.demo.huawei.config.DisConfig
import com.muxu.demo.huawei.config.StreamChannelEnum
import com.muxu.demo.huawei.dis.message.DISMessage
import com.muxu.demo.huawei.dis.message.JsonMessage
import java.util.stream.IntStream

@Slf4j
class Producer(streamChannelEnum: StreamChannelEnum) {

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

    fun produce(message: DISMessage) {
        produce(listOf(message))
    }

    fun produce(message: List<DISMessage>) {
        val putRecordsRequest = PutRecordsRequest()
        putRecordsRequest.streamName = this.streamName
        val putRecordsRequestEntryList: MutableList<PutRecordsRequestEntry> = ArrayList()
        message.forEach {
            val putRecordsRequestEntry = PutRecordsRequestEntry()
            putRecordsRequestEntry.data = it.toBuffer()
            putRecordsRequestEntry.partitionKey = it.getPartitionKey()
            putRecordsRequestEntryList.add(putRecordsRequestEntry)
        }
        putRecordsRequest.records = putRecordsRequestEntryList
        var putRecordsResult: PutRecordsResult? = null
        try {
            putRecordsResult = this.dic.putRecords(putRecordsRequest)
        } catch (e: DISClientException) {
            log.error(
                "Failed to get a normal response, please check params and retry. Error message [{}]",
                e.message,
                e
            )
        } catch (e: Exception) {
            log.error(e.message, e)
        }
        if (putRecordsResult != null) {
            log.info(
                "Put {} records[{} successful / {} failed].",
                putRecordsResult.records.size,
                putRecordsResult.records.size - putRecordsResult.failedRecordCount.get(),
                putRecordsResult.failedRecordCount
            )
            for (j in putRecordsResult.records.indices) {
                val putRecordsRequestEntry = putRecordsResult.records[j]
                if (!StringUtils.isNullOrEmpty(putRecordsRequestEntry.errorCode)) {
                    // 上传失败
                    log.error(
                        "[{}] put failed, errorCode [{}], errorMessage [{}]", String(
                            putRecordsRequestEntryList[j].data.array()
                        ),
                        putRecordsRequestEntry.errorCode,
                        putRecordsRequestEntry.errorMessage
                    )
                } else {
                    // 上传成功
                    log.info(
                        "[{}] put success, partitionId [{}], partitionKey [{}], sequenceNumber [{}]", String(
                            putRecordsRequestEntryList[j].data.array()
                        ),
                        putRecordsRequestEntry.partitionId,
                        putRecordsRequestEntryList[j].partitionKey,
                        putRecordsRequestEntry.sequenceNumber
                    )
                }
            }
        }
    }
    
    
    
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Producer(StreamChannelEnum.DEFAULT).produce(IntStream.range(1, 100).mapToObj { JsonMessage(
                """
                    {
                        "hello1": "world"
                    }
                """.trimIndent()
            ) }.toList())
        }
    }

}
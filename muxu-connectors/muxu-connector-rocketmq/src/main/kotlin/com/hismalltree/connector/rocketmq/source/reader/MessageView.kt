package com.hismalltree.connector.rocketmq.source.reader

interface MessageView {
    /**
     * Get the unique message ID.
     *
     * @return the message ID
     */
    val messageId: String?

    /**
     * Get the topic that the message belongs to.
     *
     * @return the topic
     */
    val topic: String?

    /**
     * Get the name of the broker that handles the message.
     *
     * @return the broker name
     */
    val brokerName: String?

    /**
     * Get the ID of the queue that the message is stored in.
     *
     * @return the queue ID
     */
    val queueId: Int

    /**
     * Get the offset of the message within the queue.
     *
     * @return the queue offset
     */
    val queueOffset: Long

    /**
     * Get the tag of the message, which is used for filtering.
     *
     * @return the message tag
     */
    val tag: String?

    /**
     * Get the keys of the message, which are used for partitioning and indexing.
     *
     * @return the message keys
     */
    val keys: Collection<String?>?

    /**
     * Get the size of the message in bytes.
     *
     * @return the message size
     */
    val storeSize: Int

    /**
     * Get the body of the message.
     *
     * @return the message body
     */
    val body: ByteArray?

    /**
     * Get the number of times that the message has been attempted to be delivered.
     *
     * @return the delivery attempt count
     */
    val deliveryAttempt: Int

    /**
     * Get the event time of the message, which is used for filtering and sorting.
     *
     * @return the event time
     */
    val eventTime: Long

    /**
     * Get the ingestion time of the message, which is the time that the message was received by the
     * broker.
     *
     * @return the ingestion time
     */
    val ingestionTime: Long

    /**
     * Get the properties of the message, which are set by the producer.
     *
     * @return the message properties
     */
    val properties: Map<String?, String?>?
}
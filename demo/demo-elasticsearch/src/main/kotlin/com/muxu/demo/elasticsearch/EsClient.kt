package com.muxu.demo.elasticsearch

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.elasticsearch.core.BulkRequest
import co.elastic.clients.elasticsearch.core.IndexRequest
import co.elastic.clients.json.JsonData
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.rest_client.RestClientTransport
import com.muxu.core.annotation.Slf4j
import com.muxu.core.annotation.Slf4j.Companion.log
import org.apache.http.HttpHeaders
import org.apache.http.HttpHost
import org.apache.http.HttpResponseInterceptor
import org.apache.http.entity.ContentType
import org.apache.http.message.BasicHeader
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.sniff.Sniffer
import java.io.Closeable
import java.io.StringReader

@Slf4j
class EsClient(vararg hosts: HttpHost) : Closeable {

    private val client: ElasticsearchClient;
    private val sniffer: Sniffer

    init {
        val restClient = RestClient
            .builder(*hosts)
            .setHttpClientConfigCallback { httpClientBuilder ->
                httpClientBuilder.setDefaultHeaders(
                    listOf(
                        BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString())
                    )
                ).addInterceptorLast(HttpResponseInterceptor { response, _ ->
                    response.addHeader("X-Elastic-Product", "Elasticsearch")
                })
            }
            .build()
        client = ElasticsearchClient(RestClientTransport(restClient, JacksonJsonpMapper()))
        sniffer = Sniffer.builder(restClient).build()
    }

    fun index(index: String, document: Any) {
        val response = client.index { i ->
            i.index(index)
                .document(document)
        }
        log.info("Indexed with version: {}, data: {}", response.version(), document)
    }

    fun index(index: String, documents: List<Any>) {
        val builder = BulkRequest.Builder()
        for (document in documents) {
            builder.operations { op ->
                op.index { idx ->
                    idx.index(index)
                        .document(document)
                }
            }
        }
        bulk(builder.build())
    }

    fun indexJson(index: String, document: String) {
        val input = StringReader(document)
        val builder = IndexRequest.Builder<JsonData>()
        builder.index(index)
        builder.withJson(input)
        index(builder.build())
    }

    /**
     * dsl query
     */
    fun <T> query(index: String, query: Query, resultClass: Class<T>): List<T?> {
        val searchResponse = client.search(
            { s ->
                s.index(index).query(query)
            },
            resultClass
        )
        return searchResponse.hits().hits().stream()
            .map { hit ->
                hit.source()
            }.toList()
    }

    /**
     * A bulk request can contain several kinds of operations:
     * 1. create a document, indexing it after ensuring it doesn’t already exist,
     * 2. index a document, creating it if needed and replacing it if it exists,
     * 3. update a document that already exists in place, either with a script or a partial document,
     * 4. delete a document.
     */
    private fun bulk(bulkRequest: BulkRequest) {
        val response = client.bulk(bulkRequest)
        if (response.errors()) {
            log.error("bulk has some error")
            for (item in response.items()) {
                item.error()?.let { log.error(it.reason()) }
            }
        }
    }

    private fun <T> index(indexRequest: IndexRequest<T>) {
        try {
            val response = client.index(indexRequest)
            log.info("Indexed with version: {}, data: {}", response.version(), indexRequest.document())
        } catch (e: Exception) {
            log.error("")
        }
    }

    override fun close() {
        client._transport().close()
        sniffer.close()
    }

}
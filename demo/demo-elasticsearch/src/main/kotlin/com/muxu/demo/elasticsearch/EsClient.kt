package com.muxu.demo.elasticsearch

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.rest_client.RestClientTransport
import org.apache.http.HttpHeaders
import org.apache.http.HttpHost
import org.apache.http.HttpResponseInterceptor
import org.apache.http.entity.ContentType
import org.apache.http.message.BasicHeader
import org.elasticsearch.client.RestClient

class EsClient(vararg hosts: HttpHost) {

    private val client: ElasticsearchClient;

    init {

        client = ElasticsearchClient(
            RestClientTransport(
                RestClient
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
                    .build(),
                JacksonJsonpMapper()
            )
        )
    }

    fun createIndex() {
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

}
package com.muxu.demo.elasticsearch

import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient

class ElasticSearchClient(serverHost: String) {

    private val client: RestHighLevelClient;

    init {
        client = RestHighLevelClient(
            RestClient.builder(HttpHost(serverHost))
        )
    }



}
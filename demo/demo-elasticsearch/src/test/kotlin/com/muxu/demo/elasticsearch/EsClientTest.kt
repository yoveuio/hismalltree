package com.muxu.demo.elasticsearch

import co.elastic.clients.elasticsearch._types.query_dsl.Query
import org.apache.http.HttpHost
import org.junit.Test
import kotlin.test.assertNotNull

class EsClientTest {

    private val domain = "10.174.243.97"
    private val port = 9374
    private val scheme = "http"

    @Test
    fun testConnection() {
        val client = EsClient(HttpHost(domain, port, scheme))
        val result = client.query("product",
            Query.of {
                it.term { t ->
                    t.field("name")
                        .value { v -> v.stringValue("hello") }
                }
            }, Product::class.java
        )
        assertNotNull(result)
    }

}


data class Product(
    var name: String? = null,
    var age: Int? = null
)

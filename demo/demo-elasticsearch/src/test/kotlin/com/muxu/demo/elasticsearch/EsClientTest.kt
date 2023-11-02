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
        val result = client.query(
            "product",
            Query.of {
                it.term { t ->
                    t.field("name")
                        .value { v -> v.stringValue("hello") }
                }
            }, Product::class.java
        )
        assertNotNull(result)
    }

    @Test
    fun testIndexDocument() {
        val client = EsClient(HttpHost(domain, port, scheme))
        val schema = Schema(
            name = "event_name",
            scene = Scene(
                name = "场景描述",
                conditions = listOf(
                    SceneCondition(
                        field = "key1",
                        value = "value1"
                    ),
                    SceneCondition(
                        field = "key2",
                        value = "value2"
                    )
                )
            ),
            appId = 1128,
            description = "描述",
            tags = listOf("tag1", "tag2", "tag3"),
            os = listOf("android", "ios", "macos", "windows", "linux"),
            params = listOf(
                Param(
                    name = "param1",
                    description = "属性描述1",
                    dataType = "integer"
                )
            )
        )
        client.index("schema", schema)
    }

    @Test
    fun testQuery() {
        val client = EsClient(HttpHost(domain, port, scheme))
        val schemas = client.query(
            index = "schema",
            Query.of {
                it.bool { bool ->
                    bool.should { should ->
                        should.match { match ->
                            match.field("name")
                                .query("event")
                                .fuzziness("AUTO")
                        }
                    }
                }
            },
            Schema::class.java
        )
        assertNotNull(schemas)
    }

}


data class Product(
    var name: String? = null,
    var age: Int? = null
)

data class Schema(
    var name: String? = null,
    var scene: Scene? = null,
    var appId: Long? = null,
    var description: String? = null,
    var tags: List<String> = emptyList(),
    var os: List<String> = emptyList(),
    var params: List<Param> = emptyList()
)

data class Scene(
    var name: String? = null,
    var conditions: List<SceneCondition>? = emptyList(),
)

data class SceneCondition(
    var field: String? = null,
    var value: String? = null
)

data class Param(
    var name: String? = null,
    var description: String? = null,
    var dataType: String? = null
)

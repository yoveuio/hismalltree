package com.muxu.demo.spring.repository.impl

import com.muxu.demo.spring.BaseSpringTestService
import org.junit.Test
import org.springframework.boot.test.mock.mockito.MockBean
import kotlin.test.assertNotNull

class UserRepositoryImplTest: BaseSpringTestService(){

    @MockBean
    private lateinit var repository: UserRepositoryImpl

    @Test
    fun test() {
        val listUsers = repository.listUsers(listOf(1))
        assertNotNull(listUsers)
    }

}
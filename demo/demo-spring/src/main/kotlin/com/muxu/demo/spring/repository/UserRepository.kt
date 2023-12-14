package com.muxu.demo.spring.repository

import com.muxu.demo.spring.entity.domain.User

interface UserRepository {

    fun listUsers(ids: List<Long>): List<User>

}
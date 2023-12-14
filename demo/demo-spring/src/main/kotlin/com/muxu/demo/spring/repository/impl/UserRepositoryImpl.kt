package com.muxu.demo.spring.repository.impl

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.hismalltree.demo.spring.converter.UserConvertor
import com.muxu.demo.spring.entity.db.UserPO
import com.muxu.demo.spring.entity.domain.User
import com.muxu.demo.spring.repository.UserRepository
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    var userMapper: UserMapper
) : UserRepository {

    override fun listUsers(ids: List<Long>): List<User> {
        return userMapper.selectBatchIds(ids).stream()
            .map(UserConvertor.INSTANCE::toEntity)
            .toList()
    }

    @Mapper
    interface UserMapper : BaseMapper<UserPO> {


    }

}
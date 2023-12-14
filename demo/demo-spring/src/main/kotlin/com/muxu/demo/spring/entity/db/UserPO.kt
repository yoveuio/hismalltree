package com.muxu.demo.spring.entity.db

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName


@TableName("users")
data class UserPO(

    @TableId(type = IdType.AUTO)
    var id: Long,

    var name: String,

    var age: Int

)

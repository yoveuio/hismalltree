package com.muxu.demo.spring.domain

import com.baidu.unbiz.fluentvalidator.FluentValidator
import com.muxu.demo.spring.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class ValidatorDomainService(
    var userRepository: UserRepository
) {

    fun validate() {
        FluentValidator.checkAll().doValidate()
    }

}
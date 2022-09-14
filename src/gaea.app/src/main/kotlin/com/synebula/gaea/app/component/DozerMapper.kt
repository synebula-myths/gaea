package com.synebula.gaea.app.component

import com.synebula.gaea.data.serialization.IObjectMapper
import org.dozer.DozerBeanMapper
import org.springframework.stereotype.Component

@Component
class DozerMapper : IObjectMapper {
    private val converter = DozerBeanMapper()

    override fun <T> deserialize(src: Any, targetClass: Class<T>): T = converter.map(src, targetClass)
}
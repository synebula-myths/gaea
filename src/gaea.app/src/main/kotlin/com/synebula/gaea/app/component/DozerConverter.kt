package com.synebula.gaea.app.component

import com.synebula.gaea.data.IObjectConverter
import org.dozer.DozerBeanMapper
import org.springframework.stereotype.Component

@Component
class DozerConverter : IObjectConverter {
    private val converter = DozerBeanMapper()

    override fun <T> convert(src: Any, dest: Class<T>): T = converter.map(src, dest)
}
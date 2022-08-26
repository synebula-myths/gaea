package com.synebula.gaea.jpa.proxy.method.resolver

import com.synebula.gaea.jpa.toSpecification
import com.synebula.gaea.query.Order
import com.synebula.gaea.query.Params
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.util.*
import javax.persistence.EmbeddedId
import javax.persistence.Id

/**
 * 分页方法参数映射
 */
class PageMethodResolver(targetMethodName: String, clazz: Class<*>) : AbstractMethodResolver(targetMethodName, clazz) {

    override fun mappingArguments(args: Array<Any>): Array<Any> {
        return try {
            val params: Params? = args[0] as Params?
            val specification = params!!.parameters.toSpecification(entityClazz)
            var sort = Sort.unsorted()
            for (key in params.orders.keys) {
                val direction = if (params.orders[key] === Order.ASC) Sort.Direction.ASC else Sort.Direction.DESC
                sort = sort.and(Sort.by(direction, key))
            }
            if (sort.isEmpty) {
                val fields = entityClazz.declaredFields
                for (field in fields) {
                    val isId = Arrays.stream(field.declaredAnnotations).anyMatch { annotation: Annotation ->
                        (annotation.annotationClass.java == Id::class.java
                                || annotation.annotationClass.java == EmbeddedId::class.java)
                    }
                    if (isId) {
                        sort = Sort.by(Sort.Direction.ASC, field.name)
                        break
                    }
                }
            }

            // Pageable 页面从0开始
            val pageable: Pageable = PageRequest.of(params.page - 1, params.size, sort)
            arrayOf(specification, pageable)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun mappingResult(result: Any): Any {
        val page = result as Page<*>

        // Page 页面从0开始
        return com.synebula.gaea.query.Page(page.number + 1, page.size, page.totalElements.toInt(), page.content)
    }
}
package com.synebula.gaea.domain.repository.specifications

/**
 * 逆反规约。
 *
 * @author alex
 *
 * @param T 规约对象的类型。
 *
 * @param spec 需要逆反的规约对象。
 *
 */
class NotSpecification<T>(private val spec: ISpecification<T>) : Specification<T>() {

    override fun isSatisfiedBy(obj: T): Boolean {
        return !spec.isSatisfiedBy(obj)
    }

}

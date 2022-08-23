package com.synebula.gaea.domain.repository.specifications

/**
 * 任何对象都返回假。
 *
 * @author alex
 *
 * @param T 规约对象的类型。
 */
class NoneSpecification<T> : Specification<T>() {

    override fun isSatisfiedBy(obj: T): Boolean {
        return false
    }

}

package com.synebula.gaea.domain.repository.specifications

/**
 * 表示第一个规约对象和第二个规约对象取与。
 *
 * @author alex
 *
 * @param T 规约对象的类型。
 * @param left 表达式左侧规约对象。
 * @param right 表达式右侧规约对象。
 */
class AndSpecification<T>(left: ISpecification<T>, right: ISpecification<T>) : CompositeSpecification<T>(left, right) {

    override fun isSatisfiedBy(obj: T): Boolean {
        return left.isSatisfiedBy(obj) && right.isSatisfiedBy(obj)
    }

}

package com.synebula.gaea.domain.repository.specifications

/**
 * 构造一个新的混合规约对象。
 *
 * @param T 规约对象的类型。
 *
 * @param left  表达式左侧规约对象。
 * @param right 表达式右侧规约对象。
 */
class OrSpecification<T>(left: ISpecification<T>, right: ISpecification<T>) : CompositeSpecification<T>(left, right) {
    override fun isSatisfiedBy(obj: T): Boolean {
        return left.isSatisfiedBy(obj) || right.isSatisfiedBy(obj)
    }
}

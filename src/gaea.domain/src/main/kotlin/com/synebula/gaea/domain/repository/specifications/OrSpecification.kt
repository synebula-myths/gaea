package com.synebula.gaea.domain.repository.specifications

class OrSpecification<T>
/**
 * 构造一个新的混合规约对象。
 *
 * @param left
 * 表达式左侧规约对象。
 * @param right
 * 表达式右侧规约对象。
 */
(left: ISpecification<T>, right: ISpecification<T>) : CompositeSpecification<T>(left, right) {

    override fun isSatisfiedBy(obj: T): Boolean {
        return left.isSatisfiedBy(obj) || right.isSatisfiedBy(obj)
    }
}

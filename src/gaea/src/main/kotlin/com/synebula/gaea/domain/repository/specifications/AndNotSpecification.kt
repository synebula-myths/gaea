package com.synebula.gaea.domain.repository.specifications

/**
 * 表示第一个规约对象和第二个规约对象反转的结果取与。
 *
 * @author alex
 *
 * @param <T>
 * 规约对象的类型。
 */
class AndNotSpecification<T>
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
        return left.isSatisfiedBy(obj) && NotSpecification(right).isSatisfiedBy(obj)
    }

}

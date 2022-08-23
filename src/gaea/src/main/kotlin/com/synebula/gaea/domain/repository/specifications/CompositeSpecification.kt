package com.synebula.gaea.domain.repository.specifications

/**
 * 实现了接口ICompositeSpecification<T>，混合规约的基类。
 * @author alex
 *
 * @param T 规约对象的类型。
 * @param left 表达式左侧规约对象。
 * @param right 表达式右侧规约对象。
 */
abstract class CompositeSpecification<T>(
    override val left: ISpecification<T>,
    override val right: ISpecification<T>
) : Specification<T>(), ICompositeSpecification<T>

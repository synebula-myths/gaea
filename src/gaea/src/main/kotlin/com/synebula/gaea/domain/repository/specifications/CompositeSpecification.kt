package com.synebula.gaea.domain.repository.specifications

/**
 * 实现了接口ICompositeSpecification<T>，混合规约的基类。
 * @author alex
 *
 * @param <T>
</T> */
abstract class CompositeSpecification<T>
/**
 * 构造一个新的混合规约对象。
 *
 * @param left
 * 表达式左侧规约对象。
 * @param right
 * 表达式右侧规约对象。
 */
(
        /**
         * 表达式左侧规约对象。
         */
        override val left: ISpecification<T>,
        /**
         * 表达式右侧规约对象。
         */
        override val right: ISpecification<T>) : Specification<T>(), ICompositeSpecification<T>

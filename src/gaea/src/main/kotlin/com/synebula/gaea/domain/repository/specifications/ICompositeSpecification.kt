package com.synebula.gaea.domain.repository.specifications

/**
 * 定义一个有两个规约对象混合规约接口。
 *
 * @author alex
 *
 * @param <T>
 * 规约对象的类型。
 */
interface ICompositeSpecification<T> : ISpecification<T> {

    /**
     * 获取左侧规约对象。
     *
     * @return 返回左侧规约对象。
     */
    val left: ISpecification<T>

    /**
     * 获取右侧规约对象。
     *
     * @return 返回右侧规约对象。
     */
    val right: ISpecification<T>
}

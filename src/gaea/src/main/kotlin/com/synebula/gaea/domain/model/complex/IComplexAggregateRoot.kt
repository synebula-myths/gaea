package com.synebula.gaea.domain.model.complex

/**
 * 继承本接口，说明对象为聚合根。
 *
 * @param <TKey> 主键的类型。
 * @author alex
 */
interface IComplexAggregateRoot<TKey, TSecond> : IComplexEntity<TKey, TSecond>

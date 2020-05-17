package com.synebula.gaea.domain.model

/**
 * 继承本接口，说明对象为聚合根。
 *
 * @author alex
 **/
interface IAggregateRoot<TKey> : IEntity<TKey> {

    /**
     * 实体对象是否有效。
     */
    var alive: Boolean
}

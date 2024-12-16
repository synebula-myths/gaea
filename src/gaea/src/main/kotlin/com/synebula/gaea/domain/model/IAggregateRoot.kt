package com.synebula.gaea.domain.model

import com.synebula.gaea.db.IEntity

/**
 * 继承本接口，说明对象为聚合根。
 *
 * @author alex
 **/
interface IAggregateRoot<ID> : IEntity<ID> {

    /**
     * 实体对象是否有效。
     */
    var available: Boolean
}

package com.synebula.gaea.domain.model

/**
 * 继承本接口，说明对象为实体类型。
 *
 * @author alex
 *
 * @param <ID> 主键的类型。
 **/
interface IEntity<ID> {

    /**
     * 实体ID
     */
    var id: ID?

}

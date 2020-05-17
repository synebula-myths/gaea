package com.synebula.gaea.domain.model

/**
 * 继承本接口，说明对象为实体类型。
 *
 * @author alex
 *
 * @param <TKey> 主键的类型。
 **/
interface IEntity<TKey> {

    /**
     * 实体ID
     */
    var id: TKey?

}

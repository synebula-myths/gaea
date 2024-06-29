package com.synebula.gaea.db.context

import com.synebula.gaea.db.IEntity

/**
 * 继承自IUnitOfWork，表示实现了工作单元模式的上下文接口。
 *
 * @author alex
 */
interface IDbContext : IUnitOfWork {
    /**
     * 插入单个对象。
     *
     * @param entity 需要插入的对象。
     * @return 返回原对象，如果对象ID为自增，则补充自增ID。
     */
    fun <TEntity : IEntity<ID>, ID> add(entity: TEntity, clazz: Class<TEntity>)

    /**
     * 插入多个个对象。
     *
     * @param entities 需要插入的对象。
     */
    fun <TEntity : IEntity<ID>, ID> add(entities: List<TEntity>, clazz: Class<TEntity>)

    /**
     * 更新对象。
     *
     * @param entity 需要更新的对象。
     * @return
     */
    fun <TEntity : IEntity<ID>, ID> update(entity: TEntity, clazz: Class<TEntity>)

    /**
     * 更新多个个对象。
     *
     * @param entities 需要更新的对象。
     */
    fun <TEntity : IEntity<ID>, ID> update(entities: List<TEntity>, clazz: Class<TEntity>)

    /**
     * 通过id删除该条数据
     *
     * @param id    id
     * @param clazz 操作数据的类型
     */
    fun <TEntity : IEntity<ID>, ID> remove(id: ID, clazz: Class<TEntity>)

    /**
     * 根据ID获取对象。
     *
     * @param id    id
     * @param clazz 操作数据的类型
     * @return 聚合根
     */
    fun <TEntity : IEntity<ID>, ID> get(id: ID, clazz: Class<TEntity>): TEntity?


    /**
     * 根据条件查询符合条件记录的数量
     *
     * @param params 查询条件。
     * @return int
     */
    fun <TEntity : IEntity<ID>, ID> count(params: Map<String, String>?, clazz: Class<TEntity>): Int
}

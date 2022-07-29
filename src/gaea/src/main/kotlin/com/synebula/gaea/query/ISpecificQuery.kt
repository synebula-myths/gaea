package com.synebula.gaea.query

/**
 * 查询基接口。
 * 本接口泛型定义在类上，方法中不需要显式提供聚合根的class对象，class对象作为类的成员变量声明。
 * @author alex
 */
interface ISpecificQuery<TView> {
    /**
     * 根据Key获取对象。
     *
     * @param id 对象Key。
     * @return 视图结果
     */
    fun <TView, ID> get(id: ID): TView?

    /**
     * 根据实体类条件查询所有符合条件记录
     *
     * @param params 查询条件。
     * @return 视图列表
     */
    fun <TView> list(params: Map<String, Any>?): List<TView>

    /**
     * 根据条件查询符合条件记录的数量
     *
     * @param params 查询条件。
     * @return 数量
     */
    fun <TView> count(params: Map<String, Any>?): Int

    /**
     * 根据实体类条件查询所有符合条件记录（分页查询）
     *
     * @param params 分页条件
     * @return 分页数据
     */
    fun <TView> paging(params: Params): Page<TView>

    /**
     * 查询条件范围内数据。
     * @param field 查询字段
     * @param params 查询条件
     *
     * @return 视图列表
     */
    fun <TView> range(field: String, params: List<Any>): List<TView>
}

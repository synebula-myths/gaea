package com.synebula.gaea.query

/**
 * 查询基接口, 其中方法都指定了查询的视图类型。
 *
 * @author alex
 */
interface IQuery<TView, ID> {
    /**
     * 仓储的视图类
     */
    var clazz: Class<TView>

    /**
     * 根据Key获取对象。
     *
     * @param id 对象Key。
     * @return 视图结果
     */
    fun get(id: ID): TView?

    /**
     * 根据实体类条件查询所有符合条件记录
     *`
     * @param params 查询条件。
     * @return 视图列表
     */
    fun list(params: Map<String, String>?): List<TView>

    /**
     * 根据条件查询符合条件记录的数量
     *
     * @param params 查询条件。
     * @return 数量
     */
    fun count(params: Map<String, String>?): Int

    /**
     * 根据实体类条件查询所有符合条件记录（分页查询）
     *
     * @param params 分页条件
     * @return 分页数据
     */
    fun paging(params: Params): Page<TView>

    /**
     * 查询条件范围内数据。
     * @param field 查询字段
     * @param params 查询条件
     *
     * @return 视图列表
     */
    fun range(field: String, params: List<Any>): List<TView>
}

package com.synebula.gaea.query

/**
 * 查询基接口, 其中方法都指定了查询的视图类型。
 *
 * @author alex
 */
interface IQueryTyped {
    /**
     * 根据Key获取对象。
     *
     * @param key 对象Key。
     * @return
     */
    fun <TView, TKey> get(key: TKey, clazz: Class<TView>): TView?

    /**
     * 根据实体类条件查询所有符合条件记录
     *
     * @param params 查询条件。
     * @return list
     */
    fun <TView> list(params: Map<String, Any>?, clazz: Class<TView>): List<TView>

    /**
     * 根据条件查询符合条件记录的数量
     *
     * @param params 查询条件。
     * @return int
     */
    fun <TView> count(params: Map<String, Any>?, clazz: Class<TView>): Int

    /**
     * 根据实体类条件查询所有符合条件记录（分页查询）
     *
     * @param params 分页条件
     * @return 分页数据
     */
    fun <TView> paging(params: PagingParam, clazz: Class<TView>): PagingData<TView>
}

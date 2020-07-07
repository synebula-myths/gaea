package com.synebula.gaea.query

/**
 * 查询基接口。
 *
 * @author alex
 */
interface ISpecificQuery<TView, TKey> {


    /**
     * 根据Key获取对象。
     *
     * @param key 对象Key。
     * @return
     */
    fun get(key: TKey): TView?

    /**
     * 根据实体类条件查询所有符合条件记录
     *
     * @param params 查询条件。
     * @return list
     */
    fun list(params: Map<String, Any>?): List<TView>

    /**
     * 根据条件查询符合条件记录的数量
     *
     * @param params 查询条件。
     * @return int
     */
    fun count(params: Map<String, Any>?): Int

    /**
     * 根据实体类条件查询所有符合条件记录（分页查询）
     *
     * @param param 分页条件
     * @return 分页数据
     */
    fun paging(param: Params): Page<TView>
}

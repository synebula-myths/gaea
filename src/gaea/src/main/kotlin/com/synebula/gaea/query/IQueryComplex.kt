package com.synebula.gaea.query

/**
 * 查询基接口。
 *
 * @author wxf
 */
interface IQueryComplex<TView, TKey, TSecond> {


    /**
     * 根据Key获取对象。
     *
     * @param key 对象Key。
     * @return
     */
    fun get(key: TKey, secondary: TSecond): TView

    /**
     * 根据实体类条件查询所有符合条件记录
     *
     * @param parameters 查询条件。
     * @return list
     */
    fun list(parameters: Map<String, Any>): List<TView>

    /**
     * 根据条件查询符合条件记录的数量
     *
     * @param parameters 查询条件。
     * @return int
     */
    fun count(parameters: Map<String, Any>): Int

    /**
     * 根据实体类条件查询所有符合条件记录（分页查询）
     *
     * @param param 分页条件
     * @return
     */
    fun paging(param: PagingParam): PagingData<TView>
}

package com.synebula.gaea.query

/**
 * class PagingParam
 *
 * @author alex
 * @version 0.1
 * @since 2020-05-15
 */
data class PagingParam(var page: Int = 1, var size: Int = 10) {

    /**
     * 数据索引，从0开始。表示数据在总量的第几条。（index = (page - 1) * size）
     */
    var index: Long = 0
        get() = (page.toLong() - 1) * size.toLong()
        private set

    /**
     * 排序条件。
     */
    var orderBy: MutableMap<String, OrderType> = hashMapOf()

    /**
     * 查询条件。
     */
    var parameters: MutableMap<String, Any> = hashMapOf()

    /**
     * 添加查询条件
     */
    fun addParameter(field: String, value: Any): PagingParam {
        parameters[field] = value
        return this
    }

    /**
     * 添加排序条件
     */
    fun addOrderBy(field: String, type: OrderType = OrderType.ASC): PagingParam {
        orderBy[field] = type
        return this
    }
}

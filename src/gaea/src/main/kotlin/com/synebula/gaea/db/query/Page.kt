package com.synebula.gaea.db.query

/**
 * 分页数据。
 * 真实数据行无后台遍历需求,直接使用object类型表示即可.
 *
 * @param page 页码，从1开始。
 * @param size 每页数据量。
 * @author alex
 */
data class Page<T>(var page: Int = 0, var size: Int = 0) {

    /**
     * 总数据量。
     */
    var total: Long = 0

    /**
     * 数据索引，从0开始。表示数据在总量的第几条。（index = (page - 1) * size ）
     */
    val index: Long
        get() = (page - 1) * size.toLong()

    /**
     * 结果数据。
     */
    var data = listOf<T>()


    /**
     * 数据构造。
     *
     * @param page 页码，从1开始。
     * @param size 每页数据量。
     * @param total 总数据量。
     * @param data 结果数据。
     */

    constructor(page: Int, size: Int, total: Long, data: List<T>) : this(page, size) {
        this.page = page
        this.size = size
        this.total = total
        this.data = data
    }
}

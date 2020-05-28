package com.synebula.gaea.query

/**
 * 分页数据。
 * 真实数据行无后台遍历需求,直接使用object类型表示即可.
 *
 * @author alex
 */
class Page<T> {
    /**
     * 页码，从1开始。
     */
    var page: Int = 0

    /**
     * 每页数据量。
     */

    var size: Int = 0

    /**
     * 总数据量。
     */

    var total: Int = 0

    /**
     * 数据索引，从0开始。表示数据在总量的第几条。（index = (page - 1) * size ）
     */
    var index: Int
        get() = (page - 1) * size

    /**
     * 结果数据。
     */
    var data = listOf<T>()


    /**
     * 数据构造。
     *
     * @param page 页码，从1开始。
     * @param size 每页数据量。
     */
    constructor(page: Int, size: Int) : super() {
        this.page = page
        this.size = size
        this.index = (page - 1) * size + 1
    }

    /**
     * 数据构造。
     *
     * @param page 页码，从1开始。
     * @param size 每页数据量。
     * @param total 总数据量。
     * @param data 结果数据。
     */

    constructor(page: Int, size: Int, total: Int, data: List<T>) : super() {
        this.page = page
        this.size = size
        this.total = total
        this.index = (page - 1) * size + 1
        this.data = data
    }
}

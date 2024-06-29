package com.synebula.gaea.db.query

/**
 * class 分页参数信息
 *
 * @author alex
 * @version 0.1
 * @since 2020-05-15
 */
data class Params(var page: Int = 1, var size: Int = 10) {

    private var _parameters = linkedMapOf<String, String>()
    private var _orders = linkedMapOf<String, Order>()

    /**
     * 数据索引，从0开始。表示数据在总量的第几条。（index = (page - 1) * size）
     */
    var index: Long = 0
        get() = (page.toLong() - 1) * size
        private set

    /**
     * 排序条件。
     */
    var orders: LinkedHashMap<String, Order>
        set(value) {
            this._orders = value
        }
        get() {
            this.filterOrderParams()
            return this._orders
        }

    /**
     * 查询条件。
     */
    var parameters: LinkedHashMap<String, String>
        set(value) {
            this._parameters = value
        }
        get() {
            this.filterOrderParams()
            return this._parameters
        }

    constructor(page: Int, size: Int, parameters: LinkedHashMap<String, String>) : this(page, size) {
        this.page = page
        this.size = size
        this._parameters = parameters
    }

    /**
     * 添加查询条件
     */
    fun where(field: String, value: String): Params {
        _parameters[field] = value
        return this
    }

    /**
     * 添加排序条件
     */
    fun order(field: String, order: Order = Order.ASC): Params {
        _orders[field] = order
        return this
    }

    /**
     * 过滤参数中的排序字段
     */
    private fun filterOrderParams() {
        if (this._parameters.keys.count { it.startsWith("@") } > 0) {
            val params = linkedMapOf<String, String>()
            this._parameters.forEach {
                if (it.key.startsWith("@")) {
                    this._orders[it.key.removePrefix("@")] = Order.valueOf(it.value)
                } else
                    params[it.key] = it.value
            }
            this._parameters = params
        }
    }
}

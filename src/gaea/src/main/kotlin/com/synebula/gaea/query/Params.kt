package com.synebula.gaea.query

/**
 * class 分页参数信息
 *
 * @author alex
 * @version 0.1
 * @since 2020-05-15
 */
data class Params(var page: Int = 1, var size: Int = 10) {

    private var _parameters = linkedMapOf<String, Any>()
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
            if (this._parameters.keys.count { it.startsWith("@") } > 0) {
                val params = linkedMapOf<String, Any>()
                this._parameters.forEach {
                    if (it.key.startsWith("@")) {
                        this._orders[it.key.removePrefix("@")] = Order.valueOf(it.value.toString())
                    } else
                        params[it.key] = it.value
                }
                this._parameters = params
            }
            return this._orders
        }

    /**
     * 查询条件。
     */
    var parameters: LinkedHashMap<String, Any>
        set(value) {
            this._parameters = value
        }
        get() {
            if (this._parameters.keys.count { it.startsWith("@") } > 0) {
                val params = linkedMapOf<String, Any>()
                this._parameters.forEach {
                    if (it.key.startsWith("@")) {
                        this._orders[it.key.removePrefix("@")] = Order.valueOf(it.value.toString())
                    } else
                        params[it.key] = it.value
                }
                this._parameters = params
            }
            return this._parameters
        }

    constructor(page: Int, size: Int, parameters: LinkedHashMap<String, Any>) : this(page, size) {
        this.page = page
        this.size = size
        this._parameters = parameters
    }

    /**
     * 添加查询条件
     */
    fun where(field: String, value: Any): Params {
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
}

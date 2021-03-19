package com.synebula.gaea.query

import com.synebula.gaea.query.type.Order

/**
 * class 分页参数信息
 *
 * @author alex
 * @version 0.1
 * @since 2020-05-15
 */
data class Params(var page: Int = 1, var size: Int = 10) {

    private var _parameters = mutableMapOf<String, Any>()
    private var _orders = mutableMapOf<String, Order>()

    /**
     * 数据索引，从0开始。表示数据在总量的第几条。（index = (page - 1) * size）
     */
    var index: Int = 0
        get() = (page - 1) * size
        private set

    /**
     * 排序条件。
     */
    var orders: Map<String, Order>
        set(value) {
            this._orders = value.toMutableMap()
        }
        get() {
            if (this._parameters.keys.count { it.startsWith("@") } > 0) {
                val params = mutableMapOf<String, Any>()
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
    var parameters: Map<String, Any>
        set(value) {
            this._parameters = value.toMutableMap()
        }
        get() {
            if (this._parameters.keys.count { it.startsWith("@") } > 0) {
                val params = mutableMapOf<String, Any>()
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

package com.synebula.gaea.mongo

import com.synebula.gaea.query.Operator
import com.synebula.gaea.query.OrderType
import com.synebula.gaea.query.Where
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import java.lang.reflect.Field


/**
 * 获取查询字段列表
 *
 * @param fields 字段列表
 */
fun Query.select(fields: Array<String>): Query {
    fields.forEach {
        this.fields().include(it)
    }
    return this
}

/**
 * 根据参数获取查询条件
 *
 * @param params 参数列表
 */
fun Query.where(params: Map<String, Any>?, onWhere: ((v: String) -> Operator) = { Operator.eq }): Query {
    val criteria = Criteria()
    if (params != null) {
        for (param in params) {
            val where = onWhere(param.key)
            when (where) {
                Operator.eq -> criteria.and(param.key).`is`(param.value)
                Operator.ne -> criteria.and(param.key).ne(param.value)
                Operator.lt -> criteria.and(param.key).lt(param.value)
                Operator.gt -> criteria.and(param.key).gt(param.value)
                Operator.lte -> criteria.and(param.key).lte(param.value)
                Operator.gte -> criteria.and(param.key).gte(param.value)
                Operator.like -> criteria.and(param.key).regex(param.value.toString())
            }

        }
    }
    return this.addCriteria(criteria)
}

/**
 * 根据参数获取查询条件
 *
 * @param params 参数列表
 */
fun Query.where(params: Map<String, Any>?, clazz: Class<*>): Query {
    var field: Field?
    var where: Where?
    return this.where(params) { name ->
        field = clazz.getDeclaredField(name)
        where = field?.getDeclaredAnnotation(Where::class.java)
        if (where == null) Operator.eq else where!!.operator
    }
}

/**
 * 获取ID查询条件
 *
 * @param id 业务ID
 */
fun <TKey> whereId(id: TKey): Query = Query.query(Criteria.where("_id").`is`(id))

/**
 * 获取排序对象
 *
 * @param orders 排序条件字段
 */
fun order(orders: Map<String, OrderType>?): Sort {
    val orderList = mutableListOf<Sort.Order>()
    orders?.forEach {
        orderList.add(Sort.Order(Sort.Direction.valueOf(it.value.name), it.key))
    }
    return if (orderList.size == 0)
        Sort.by("_id")
    else
        Sort.by(orderList)
}

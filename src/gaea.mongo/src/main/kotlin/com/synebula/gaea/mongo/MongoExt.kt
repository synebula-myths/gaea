package com.synebula.gaea.mongo

import com.synebula.gaea.data.date.DateTime
import com.synebula.gaea.query.Operator
import com.synebula.gaea.query.OrderType
import com.synebula.gaea.query.Where
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import java.lang.reflect.Field
import java.util.*


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
 * @param onWhere 获取字段查询方式的方法
 */
fun Query.where(
    params: Map<String, Any>?,
    onWhere: ((v: String) -> Operator) = { Operator.default },
    onFieldType: ((v: String) -> Class<*>?) = { null }
): Query {
    val list = arrayListOf<Criteria>()
    if (params != null) {
        for (param in params) {
            val key = param.key
            var value = param.value
            val fieldType = onFieldType(key)
            if (fieldType != null && value.javaClass != fieldType && fieldType == Date::class.java) {
                value = DateTime(value.toString(), "yyyy-MM-dd").date
            }
            when (onWhere(key)) {
                Operator.eq -> list.add(Criteria.where(key).`is`(value))
                Operator.ne -> list.add(Criteria.where(key).ne(value))
                Operator.lt -> list.add(Criteria.where(key).lt(value))
                Operator.gt -> list.add(Criteria.where(key).gt(value))
                Operator.lte -> list.add(Criteria.where(key).lte(value))
                Operator.gte -> list.add(Criteria.where(key).gte(value))
                Operator.like -> list.add(Criteria.where(key).regex(value.toString()))
                Operator.default -> rangeWhere(param.key, value, onFieldType(param.key), list)
            }
        }
    }
    return this.addCriteria(Criteria().andOperator(*list.toTypedArray()))
}

private fun rangeWhere(key: String, value: Any, fieldType: Class<*>?, list: MutableList<Criteria>) {
    val rangeStartSuffix = "[0]" //范围查询开始后缀
    val rangeEndSuffix = "[1]" //范围查询结束后缀
    var condition = value
    if (value.javaClass != fieldType && fieldType == Date::class.java) {
        condition = DateTime(value.toString(), "yyyy-MM-dd").date
    }
    when {
        //以范围查询开始后缀结尾表示要用大于或等于查询方式
        key.endsWith(rangeStartSuffix) ->
            list.add(
                Criteria.where(key.removeSuffix(rangeStartSuffix)).gte(condition)
            )
        //以范围查询结束后缀结尾表示要用小于或等于查询方式
        key.endsWith(rangeEndSuffix) ->
            list.add(
                Criteria.where(key.removeSuffix(rangeEndSuffix)).lte(condition)
            )
        else -> list.add(Criteria.where(key).`is`(value))
    }
}

/**
 * 根据参数获取查询条件
 *
 * @param params 参数列表
 */
fun Query.where(params: Map<String, Any>?, clazz: Class<*>): Query {
    var field: Field?
    var where: Where?
    return this.where(params, { name ->
        field = clazz.declaredFields.find { it.name == name }
        where = field?.getDeclaredAnnotation(Where::class.java)
        if (where == null) Operator.default else where!!.operator
    })
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

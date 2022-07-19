package com.synebula.gaea.mongo

import com.synebula.gaea.data.date.DateTime
import com.synebula.gaea.query.annotation.Where
import com.synebula.gaea.query.type.Operator
import com.synebula.gaea.query.type.Order
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
        onWhere: ((v: String) -> Where?) = { null },
        onFieldType: ((v: String) -> Class<*>?) = { null }
): Query {
    val list = arrayListOf<Criteria>()
    if (params != null) {
        for (param in params) {
            val key = param.key
            var value = param.value

            //日期类型特殊处理为String类型
            val fieldType = onFieldType(key)
            if (fieldType != null && value.javaClass != fieldType) {
                when (fieldType) {
                    Date::class.java -> value = DateTime(value.toString(), "yyyy-MM-ddTHH:mm:ss").date
                    Int::class.java -> value = value.toString().toInt()
                    Integer::class.java -> value = value.toString().toInt()
                }
            }

            val where = onWhere(key)
            if (where == null) {
                list.add(tryRangeWhere(param.key, value, onFieldType))
            } else {
                //判断执行查询子元素还是本字段
                val field = where.children.ifEmpty { key }
                var criteria = Criteria.where(field)
                criteria = when (where.operator) {
                    Operator.eq -> criteria.`is`(value)
                    Operator.ne -> criteria.ne(value)
                    Operator.lt -> criteria.lt(value)
                    Operator.gt -> criteria.gt(value)
                    Operator.lte -> criteria.lte(value)
                    Operator.gte -> criteria.gte(value)
                    Operator.like -> criteria.regex(value.toString(), if (where.sensitiveCase) "" else "i")
                    Operator.default -> tryRangeWhere(param.key, value, onFieldType)
                }
                list.add(if (where.children.isEmpty()) criteria else Criteria.where(key).elemMatch(criteria))
            }
        }
    }
    val criteria = Criteria()
    if (list.isNotEmpty()) criteria.andOperator(*list.toTypedArray())
    return this.addCriteria(criteria)
}

/**
 * 尝试范围查询，失败则返回正常查询条件。
 */
private fun tryRangeWhere(key: String, value: Any, onFieldType: ((v: String) -> Class<*>?) = { null }): Criteria {
    val rangeStartSuffix = "[0]" //范围查询开始后缀
    val rangeEndSuffix = "[1]" //范围查询结束后缀
    var condition = value
    val realKey = key.removeSuffix(rangeStartSuffix).removeSuffix(rangeEndSuffix)
    val fieldType = onFieldType(realKey)
    if (fieldType != null && value.javaClass != fieldType && fieldType == Date::class.java) {
        condition = DateTime(value.toString(), "yyyy-MM-dd HH:mm:ss").date
    }

    return when {
        //以范围查询开始后缀结尾表示要用大于或等于查询方式
        key.endsWith(rangeStartSuffix) -> Criteria.where(realKey).gte(condition)
        //以范围查询结束后缀结尾表示要用小于或等于查询方式
        key.endsWith(rangeEndSuffix) -> Criteria.where(realKey).lte(condition)
        else -> Criteria.where(key).`is`(value)
    }
}

/**
 * 根据参数获取查询条件
 *
 * @param params 参数列表
 */
fun Query.where(params: Map<String, Any>?, clazz: Class<*>): Query {
    var field: Field?
    return this.where(params, { name ->
        field = clazz.declaredFields.find { it.name == name }
        field?.getDeclaredAnnotation(Where::class.java)
    }, { name -> clazz.declaredFields.find { it.name == name }?.type })
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
fun order(orders: Map<String, Order>?): Sort {
    val orderList = mutableListOf<Sort.Order>()
    orders?.forEach {
        orderList.add(Sort.Order(Sort.Direction.valueOf(it.value.name), it.key))
    }
    return if (orderList.size == 0)
        Sort.by(Sort.Direction.DESC, "_id")
    else
        Sort.by(orderList)
}

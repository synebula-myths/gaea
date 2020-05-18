package com.synebula.gaea.query.mongo

import com.synebula.gaea.log.ILogger
import com.synebula.gaea.query.OrderType
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import java.lang.reflect.Field

/**
 * 声明了Mongo查询的特有方法
 */
interface IMongoQuery {
    /**
     * 日志组件
     */
    var logger: ILogger?

    /**
     * 系统类型
     */
    private fun systemClass() = arrayOf(
            "String",
            "Date",
            "Int",
            "Double",
            "Float",
            "BigDecimal",
            "Decimal")

    /**
     * 获取ID查询条件
     *
     * @param id 业务ID
     */
    fun <TKey> idQuery(id: TKey): Query = Query(Criteria("_id").isEqualTo(id))

    /**
     * 获取视图对象的字段列表
     *
     * @param clazz 视图对象类型
     */
    fun <TView> fields(clazz: Class<TView>): List<String> {
        return analyseFields(clazz.declaredFields)
    }

    /**
     * 获取查询条件
     *
     * @param query 查询条件
     * @param fields 字段列表
     */
    fun select(query: Query, fields: Array<String>): Query {
        fields.forEach {
            query.fields().include(it)
        }
        return query
    }

    /**
     * 根据参数获取查询条件
     *
     * @param query 查询条件
     * @param params 参数列表
     * @param clazz 视图类对象
     */
    fun <TView> where(query: Query, params: Map<String, Any>?, clazz: Class<TView>): Query {
        val criteria = Criteria()
        if (params != null) {
            for (param in params) {
                val value = this.changeFieldType(param.key, param.value, clazz)
                criteria.and(param.key).isEqualTo(value)
            }
        }
        return query.addCriteria(criteria)
    }

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

    /**
     * 分析视图字段对象，获取深层字段列表
     *
     * @param fields 需要分析的视图字段
     */
    fun analyseFields(fields: Array<Field>): List<String> {
        val names = mutableListOf<String>()
        fields.forEach { field ->
            names.add(field.name)
            if (!field.type.isPrimitive
                    && !field.type.isArray
                    && !this.systemClass().contains(field.type.simpleName))
                names.addAll(this.analyseFields(field.type.declaredFields).map { "${field.name}.$it" })
        }
        return names
    }

    /**
     * 转换查询条件指定字段的类型
     *
     * @param key 字段名称
     * @param value 字段值
     * @param clazz 视图类对象
     */
    fun <TView> changeFieldType(key: String, value: Any, clazz: Class<TView>): Any? {
        val getter = clazz.getMethod("get${key.substring(0, 1).toUpperCase()}${key.substring(1)}")
        return this.convertType(value.toString(), getter.returnType)
    }


    /**
     * 转换String到指定类型
     *
     * @param value 字段值
     * @param clazz 视图类对象
     */
    fun convertType(value: String, clazz: Class<*>): Any? {
        if (!clazz.isPrimitive) { // 判断基本类型
            if (clazz == String::class.java) { // 如果是string则直接返回
                return value
            }
            //  如果不为null 则通过反射实例一个对象返回
            return if ("" == value) null else clazz.getConstructor(String::class.java).newInstance(value)
        }

        // 下面处理基本类型，返回包装类
        return when (clazz.name) {
            "int" -> Integer.parseInt(value)
            "byte" -> java.lang.Byte.parseByte(value)
            "boolean" -> java.lang.Boolean.parseBoolean(value)
            "double" -> java.lang.Double.parseDouble(value)
            "float" -> java.lang.Float.parseFloat(value)
            "long" -> java.lang.Long.parseLong(value)
            "short" -> java.lang.Short.parseShort(value)
            else -> value
        }
    }

}

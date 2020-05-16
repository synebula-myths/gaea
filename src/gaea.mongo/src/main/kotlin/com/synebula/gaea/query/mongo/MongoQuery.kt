package com.synebula.gaea.query.mongo

import com.synebula.gaea.query.IQuery
import com.synebula.gaea.query.OrderType
import com.synebula.gaea.query.PagingData
import com.synebula.gaea.query.PagingParam
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType


open class MongoQuery<TView>(var collection: String, var repo: MongoTemplate) : IQuery<TView, String> {


    @Suppress("UNCHECKED_CAST")
    protected val viewClass: Class<TView> = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<TView>

    private val _systemClass = arrayOf(
            "String",
            "Date",
            "Int",
            "Double",
            "Float",
            "BigDecimal",
            "Decimal")


    override fun list(params: Map<String, Any>?): List<TView> {
        val viewFields = this.viewFields()
        val query = Query()
        this.where(query, params)
        this.select(query, viewFields.toTypedArray())
        return this.repo.find(query, this.viewClass, this.collection)
    }

    override fun count(params: Map<String, Any>?): Int {
        val query = Query()
        return this.repo.count(where(query, params), this.collection).toInt()
    }

    override fun paging(params: PagingParam): PagingData<TView> {
        val viewFields = this.viewFields()
        val result = PagingData<TView>(1, 10)
        result.size = params.size
        result.page = params.page
        val query = Query()
        this.where(query, params.parameters)
        result.total = this.repo.count(query, this.collection).toInt()
        this.select(query, viewFields.toTypedArray())
        query.with(order(params.orderBy))
        query.skip(params.index).limit(params.size)
        result.data = this.repo.find(query, this.viewClass, this.collection)
        return result
    }

    override fun get(key: String): TView? {
        return this.repo.findOne(Query.query(Criteria.where("_id").isEqualTo(key))
                , this.viewClass, this.collection)
    }

    protected fun viewFields(): List<String> {
        return traversalFields(viewClass.declaredFields)
    }

    private fun traversalFields(fields: Array<Field>): List<String> {
        val names = mutableListOf<String>()
        fields.forEach { field ->
            names.add(field.name)
            if (!field.type.isPrimitive
                    && !field.type.isArray
                    && !this._systemClass.contains(field.type.simpleName))
                names.addAll(this.traversalFields(field.type.declaredFields).map { "${field.name}.$it" })
        }
        return names
    }

    protected open fun where(query: Query, params: Map<String, Any>?): Query {
        val criteria = Criteria()
        if (params != null) {
            for (param in params) {
                val value = this.convertFieldValueType(param.key, param.value)
                criteria.and(param.key).isEqualTo(value)
            }
        }
        return query.addCriteria(criteria)
    }

    protected fun convertFieldValueType(key: String, value: Any): Any? {
        val getter = this.viewClass.getMethod("get${key.substring(0, 1).toUpperCase()}${key.substring(1)}")
        return this.convertClass(getter.returnType, value.toString())
    }

    protected open fun select(query: Query, fields: Array<String>): Query {
        fields.forEach {
            query.fields().include(it)
        }
        return query
    }

    protected open fun order(orders: MutableMap<String, OrderType>?): Sort {
        val orderList = mutableListOf<Sort.Order>()
        orders?.forEach() {
            orderList.add(Sort.Order(Sort.Direction.valueOf(it.value.name), it.key))
        }
        return if (orderList.size == 0)
            Sort.by("_id")
        else
            Sort.by(orderList)
    }

    private fun convertClass(type: Class<*>, value: String): Any? {

        if (!type.isPrimitive) { // 判断基本类型
            if (type == String::class.java) { // 如果是string则直接返回
                return value
            }
            //  如果不为null 则通过反射实例一个对象返回
            return if ("" == value) null else type.getConstructor(String::class.java).newInstance(value)
        }

        // 下面处理基本类型，返回包装类
        return when (type.name) {
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

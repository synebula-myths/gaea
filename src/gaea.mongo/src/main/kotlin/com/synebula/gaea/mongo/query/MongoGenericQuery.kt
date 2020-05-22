package com.synebula.gaea.mongo.query

import com.synebula.gaea.extension.fields
import com.synebula.gaea.extension.firstCharLowerCase
import com.synebula.gaea.log.ILogger
import com.synebula.gaea.mongo.order
import com.synebula.gaea.mongo.select
import com.synebula.gaea.mongo.where
import com.synebula.gaea.mongo.whereId
import com.synebula.gaea.query.IGenericQuery
import com.synebula.gaea.query.PagingData
import com.synebula.gaea.query.PagingParam
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query

/**
 * 实现IQuery的Mongo查询类
 * @param template MongoRepo对象
 * @param logger 日志组件
 */
open class MongoGenericQuery<TView>(var template: MongoTemplate, var logger: ILogger? = null) : IGenericQuery<TView, String> {
    /**
     * 查询的对象类
     */
    var clazz: Class<TView>? = null

    private var _collection = ""

    /**
     * 查询的集合名称
     */
    var collection: String
        set(value) {
            this._collection = value
        }
        get() = if (this._collection.isNotEmpty())
            this._collection
        else {
            if (this.clazz != null)
                this.clazz!!.simpleName.removeSuffix("View").firstCharLowerCase()
            else
                ""
        }

    /**
     * 构造方法
     *
     * @param clazz 视图对象类型
     * @param query MongoRepo对象
     */
    constructor(clazz: Class<TView>, query: MongoTemplate)
            : this(query) {
        this.clazz = clazz
    }

    /**
     * 构造方法
     *
     * @param collection 查询的集合名称
     * @param query MongoRepo对象
     */
    constructor(collection: String, query: MongoTemplate)
            : this(query) {
        this.collection = collection
    }

    /**
     * 构造方法
     *
     * @param collection 查询的集合名称
     * @param clazz 视图对象类型
     * @param query MongoRepo对象
     */
    constructor(collection: String, clazz: Class<TView>, query: MongoTemplate)
            : this(clazz, query) {
        this.collection = collection
    }


    override fun list(params: Map<String, Any>?): List<TView> {
        this.check()
        return if (this.clazz != null) {
            val fields = this.clazz!!.fields()
            val query = Query()
            query.select(fields.toTypedArray())
            query.where(params, this.clazz!!)
            this.template.find(query, this.clazz!!, this.collection)
        } else listOf()
    }

    override fun count(params: Map<String, Any>?): Int {
        this.check()
        return if (this.clazz != null) {
            val query = Query()
            this.template.count(query.where(params, this.clazz!!), this.collection).toInt()
        } else 0
    }

    override fun paging(param: PagingParam): PagingData<TView> {
        this.check()
        return if (this.clazz != null) {
            val query = Query()
            val fields = this.clazz!!.fields()
            val result = PagingData<TView>(param.page, param.size)
            result.total = this.count(param.parameters)
            query.where(param.parameters, this.clazz!!)
            query.select(fields.toTypedArray())
            query.with(order(param.orderBy))
            query.skip(param.index).limit(param.size)
            result.data = this.template.find(query, this.clazz!!, this.collection)
            result
        } else PagingData(1, 10)
    }

    override fun get(key: String): TView? {
        this.check()
        return if (this.clazz != null) {
            val view = this.template.findOne(whereId(key), this.clazz!!, this.collection)
            view
        } else null
    }

    protected fun check() {
        if (this.clazz == null)
            throw RuntimeException("[${this.javaClass.name}] 没有声明查询View的类型")
        if (this._collection.isEmpty())
            this.logger?.warn(this, "查询集合参数[collection]值为空, 尝试使用View<${this.clazz?.name}>名称解析集合")
    }

}

package com.synebula.gaea.mongo.query

import com.synebula.gaea.extension.fields
import com.synebula.gaea.extension.firstCharLowerCase
import com.synebula.gaea.log.ILogger
import com.synebula.gaea.mongo.order
import com.synebula.gaea.mongo.select
import com.synebula.gaea.mongo.where
import com.synebula.gaea.mongo.whereId
import com.synebula.gaea.query.IQuery
import com.synebula.gaea.query.PagingData
import com.synebula.gaea.query.PagingParam
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query

/**
 * 实现IQuery的Mongo查询类
 * @param repo MongoRepo对象
 */
open class MongoQuery<TView>(var repo: MongoTemplate, var logger: ILogger? = null) : IQuery<TView, String> {
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
     * @param repo MongoRepo对象
     */
    constructor(clazz: Class<TView>, repo: MongoTemplate)
            : this(repo) {
        this.clazz = clazz
    }

    /**
     * 构造方法
     *
     * @param collection 查询的集合名称
     * @param repo MongoRepo对象
     */
    constructor(collection: String, repo: MongoTemplate)
            : this(repo) {
        this.collection = collection
    }

    /**
     * 构造方法
     *
     * @param clazz 视图对象类型
     * @param repo MongoRepo对象
     */
    constructor(collection: String, clazz: Class<TView>, repo: MongoTemplate)
            : this(clazz, repo) {
        this.collection = collection
    }


    override fun list(params: Map<String, Any>?): List<TView> {
        this.check()
        return if (this.clazz != null) {
            val fields = this.clazz!!.fields()
            val query = Query()
            query.select(fields.toTypedArray())
            query.where(params)
            this.repo.find(query, this.clazz!!, this.collection)
        } else listOf()
    }

    override fun count(params: Map<String, Any>?): Int {
        this.check()
        return if (this.clazz != null) {
            val query = Query()
            this.repo.count(query.where(params), this.collection).toInt()
        } else 0
    }

    override fun paging(params: PagingParam): PagingData<TView> {
        this.check()
        return if (this.clazz != null) {
            val query = Query()
            val fields = this.clazz!!.fields()
            val result = PagingData<TView>(params.page, params.size)
            query.where(params.parameters)
            result.total = this.count(params.parameters)
            query.select(fields.toTypedArray())
            query.with(order(params.orderBy))
            query.skip(params.index).limit(params.size)
            result.data = this.repo.find(query, this.clazz!!, this.collection)
            result
        } else PagingData(1, 10)
    }

    override fun get(key: String): TView? {
        this.check()
        return if (this.clazz != null) {
            val view = this.repo.findOne(whereId(key), this.clazz!!, this.collection)
            view
        } else null
    }

    private fun check() {
        if (this.clazz == null)
            throw RuntimeException("[${this.javaClass.name}] 没有声明查询View的类型")
        if (this._collection.isEmpty())
            this.logger?.warn(this, "查询集合参数[collection]值为空, 尝试使用View<${this.clazz?.name}>名称解析集合")
    }

}

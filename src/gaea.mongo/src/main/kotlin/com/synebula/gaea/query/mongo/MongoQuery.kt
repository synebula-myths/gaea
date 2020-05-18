package com.synebula.gaea.query.mongo

import com.synebula.gaea.extension.firstCharLowerCase
import com.synebula.gaea.log.ILogger
import com.synebula.gaea.query.IQuery
import com.synebula.gaea.query.PagingData
import com.synebula.gaea.query.PagingParam
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query

/**
 * 实现IQuery的Mongo查询类
 * @param repo MongoRepo对象
 */
open class MongoQuery<TView>(var repo: MongoTemplate, override var logger: ILogger? = null) :
        IQuery<TView, String>, IMongoQuery {
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
            val viewFields = this.fields(this.clazz!!)
            val query = Query()
            this.where(query, params, this.clazz!!)
            this.select(query, viewFields.toTypedArray())
            this.repo.find(query, this.clazz!!, this.collection)
        } else listOf()
    }

    override fun count(params: Map<String, Any>?): Int {
        this.check()
        return if (this.clazz != null) {
            val query = Query()
            this.repo.count(where(query, params, this.clazz!!), this.collection).toInt()
        } else 0
    }

    override fun paging(params: PagingParam): PagingData<TView> {
        this.check()
        return if (this.clazz != null) {
            val viewFields = this.fields(this.clazz!!)
            val result = PagingData<TView>(1, 10)
            result.size = params.size
            result.page = params.page
            val query = Query()
            this.where(query, params.parameters, this.clazz!!)
            result.total = this.repo.count(query, this.collection).toInt()
            this.select(query, viewFields.toTypedArray())
            query.with(order(params.orderBy))
            query.skip(params.index).limit(params.size)
            result.data = this.repo.find(query, this.clazz!!, this.collection)
            result
        } else PagingData(1, 10)
    }

    override fun get(key: String): TView? {
        this.check()
        return if (this.clazz != null) {
            val view = this.repo.findOne(idQuery(key), this.clazz!!, this.collection)
            view
        } else null
    }

    private fun check() {
        if (this.clazz == null)
            throw RuntimeException("[${this.javaClass.name}] 没有声明查询View的类型")
        if (this._collection.isEmpty())
            this.logger?.warn(this, null, "[${this.clazz!!.name}]没有声明查询集合名称, 后续尝试使用View对象名称解析集合")
    }

}

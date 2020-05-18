package com.synebula.gaea.query.mongo

import com.synebula.gaea.log.ILogger
import com.synebula.gaea.query.IQueryTyped
import com.synebula.gaea.query.PagingData
import com.synebula.gaea.query.PagingParam
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query

/**
 * 实现IQuery的Mongo查询类
 * @param repo MongoRepo对象
 */

open class MongoQueryTyped(var repo: MongoTemplate, override var logger: ILogger? = null) : IQueryTyped, IMongoQuery {

    override fun <TView, TKey> list(params: Map<String, Any>?, clazz: Class<TView>): List<TView> {
        val viewFields = this.fields(clazz)
        val query = Query()
        this.where(query, params, clazz)
        this.select(query, viewFields.toTypedArray())
        return this.repo.find(query, clazz, clazz.simpleName)
    }

    override fun <TView, TKey> count(params: Map<String, Any>?, clazz: Class<TView>): Int {
        val query = Query()
        return this.repo.count(this.where(query, params, clazz), clazz.simpleName).toInt()
    }

    override fun <TView, TKey> paging(params: PagingParam, clazz: Class<TView>): PagingData<TView> {
        val viewFields = this.fields(clazz)
        val result = PagingData<TView>(1, 10)
        result.size = params.size
        result.page = params.page
        val query = Query()
        this.where(query, params.parameters, clazz)
        result.total = this.repo.count(query, clazz.simpleName).toInt()
        this.select(query, viewFields.toTypedArray())
        query.with(order(params.orderBy))
        query.skip(params.index).limit(params.size)
        result.data = this.repo.find(query, clazz, clazz.simpleName)
        return result
    }

    override fun <TView, TKey> get(key: TKey, clazz: Class<TView>): TView? {
        return this.repo.findOne(idQuery(key), clazz, clazz.simpleName)
    }
}

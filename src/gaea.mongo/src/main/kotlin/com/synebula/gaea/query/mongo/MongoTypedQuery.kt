package com.synebula.gaea.query.mongo

import com.synebula.gaea.log.ILogger
import com.synebula.gaea.query.*
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import java.lang.reflect.Field

/**
 * 实现IQuery的Mongo查询类
 * @param repo MongoRepo对象
 */

open class MongoTypedQuery<TView>(var repo: MongoTemplate) : ITypedQuery<TView, String>, IMongoQuery<TView> {
    override var logger: ILogger? = null

    override fun list(params: Map<String, Any>?, clazz: Class<TView>): List<TView> {
        val viewFields = this.fields(clazz)
        val query = Query()
        this.where(query, params, clazz)
        this.select(query, viewFields.toTypedArray())
        return this.repo.find(query, clazz, clazz.simpleName)
    }

    override fun count(params: Map<String, Any>?, clazz: Class<TView>): Int {
        val query = Query()
        return this.repo.count(this.where(query, params, clazz), clazz.simpleName).toInt()
    }

    override fun paging(params: PagingParam, clazz: Class<TView>): PagingData<TView> {
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

    override fun get(key: String, clazz: Class<TView>): TView? {
        return this.repo.findOne(idQuery(key), clazz, clazz.simpleName)
    }

}

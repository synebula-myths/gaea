package com.synebula.gaea.query.mongo

import com.synebula.gaea.extension.firstCharLowerCase
import com.synebula.gaea.log.ILogger
import com.synebula.gaea.query.IQueryTyped
import com.synebula.gaea.query.PagingData
import com.synebula.gaea.query.PagingParam
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import java.lang.RuntimeException

/**
 * 实现IQuery的Mongo查询类
 * @param repo MongoRepo对象
 */

open class MongoQueryTyped(var repo: MongoTemplate, override var logger: ILogger? = null) : IQueryTyped, IMongoQuery {

    /**
     * 查询的集合名称
     *
     * 若没有为成员变量collection赋值，会尝试使用View名称解析集合名称。
     * 规则为移除View后缀并首字母小写，此时使用
     */
    var collection: String = ""

    /**
     * 使用View解析是collection时是否校验存在，默认不校验
     */
    var validViewCollection = false

    override fun <TView, TKey> list(params: Map<String, Any>?, clazz: Class<TView>): List<TView> {
        val viewFields = this.fields(clazz)
        val query = Query()
        this.where(query, params, clazz)
        this.select(query, viewFields.toTypedArray())
        return this.repo.find(query, clazz, this.collection(clazz))
    }

    override fun <TView, TKey> count(params: Map<String, Any>?, clazz: Class<TView>): Int {
        val query = Query()
        return this.repo.count(this.where(query, params, clazz), this.collection(clazz)).toInt()
    }

    override fun <TView, TKey> paging(params: PagingParam, clazz: Class<TView>): PagingData<TView> {
        val viewFields = this.fields(clazz)
        val result = PagingData<TView>(1, 10)
        result.size = params.size
        result.page = params.page
        val query = Query()
        this.where(query, params.parameters, clazz)
        result.total = this.count<TView, TKey>(params.parameters, clazz)
        this.select(query, viewFields.toTypedArray())
        query.with(order(params.orderBy))
        query.skip(params.index).limit(params.size)
        result.data = this.repo.find(query, clazz, this.collection(clazz))
        return result
    }

    override fun <TView, TKey> get(key: TKey, clazz: Class<TView>): TView? {
        return this.repo.findOne(idQuery(key), clazz, this.collection(clazz))
    }

    /**
     * 获取collection
     */
    protected fun <TView> collection(clazz: Class<TView>): String {
        return if (this.collection.isEmpty()) {
            this.logger?.warn(this, "查询集合参数[collection]值为空, 尝试使用View<${clazz.name}>名称解析集合")
            val collection = clazz.simpleName.removeSuffix("View").firstCharLowerCase()
            if (!validViewCollection || this.repo.collectionExists(collection))
                collection
            else {
                throw RuntimeException("找不到名为[$collection]的集合")
            }
        } else
            this.collection
    }
}

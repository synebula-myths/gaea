package com.synebula.gaea.mongo.query

import com.synebula.gaea.extension.fieldNames
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
import java.lang.RuntimeException

/**
 * 实现IQuery的Mongo查询类
 * @param repo MongoRepo对象
 */

open class MongoQuery(var repo: MongoTemplate, var logger: ILogger? = null) : IQuery {

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

    override fun <TView> list(params: Map<String, Any>?, clazz: Class<TView>): List<TView> {
        val fields = clazz.fieldNames()
        val query = Query()
        query.where(params, clazz)
        query.select(fields.toTypedArray())
        return this.repo.find(query, clazz, this.collection(clazz))
    }

    override fun <TView> count(params: Map<String, Any>?, clazz: Class<TView>): Int {
        val query = Query()
        return this.repo.count(query.where(params, clazz), this.collection(clazz)).toInt()
    }

    override fun <TView> paging(param: PagingParam, clazz: Class<TView>): PagingData<TView> {
        val fields = clazz.fieldNames()
        val result = PagingData<TView>(param.page, param.size)
        result.total = this.count(param.parameters, clazz)
        val query = Query()
        query.where(param.parameters, clazz)
        query.select(fields.toTypedArray())
        query.with(order(param.orderBy))
        query.skip(param.index).limit(param.size)
        result.data = this.repo.find(query, clazz, this.collection(clazz))
        return result
    }

    override fun <TView, TKey> get(key: TKey, clazz: Class<TView>): TView? {
        return this.repo.findOne(whereId(key), clazz, this.collection(clazz))
    }

    /**
     * 获取collection
     */
    protected fun <TView> collection(clazz: Class<TView>): String {
        return if (this.collection.isEmpty()) {
            this.logger?.info(this, "查询集合参数[collection]值为空, 尝试使用View<${clazz.name}>名称解析集合")
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

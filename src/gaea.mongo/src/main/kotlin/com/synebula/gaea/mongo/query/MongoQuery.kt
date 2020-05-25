package com.synebula.gaea.mongo.query

import com.synebula.gaea.extension.fieldNames
import com.synebula.gaea.extension.firstCharLowerCase
import com.synebula.gaea.log.ILogger
import com.synebula.gaea.mongo.*
import com.synebula.gaea.mongo.Collection
import com.synebula.gaea.query.IQuery
import com.synebula.gaea.query.PagingData
import com.synebula.gaea.query.PagingParam
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query

/**
 * 实现IQuery的Mongo查询类
 * @param repo MongoRepo对象
 */

open class MongoQuery(var repo: MongoTemplate, var logger: ILogger? = null) : IQuery {

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
        val query = Query()
        val fields = clazz.fieldNames()
        val result = PagingData<TView>(param.page, param.size)
        result.total = this.count(param.parameters, clazz)
        //如果总数和索引相同，说明该页没有数据，直接跳到上一页
        if (result.total == result.index) {
            param.page -= 1
            result.page -= 1
        }
        query.select(fields.toTypedArray())
        query.where(param.parameters, clazz)
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
        val collection: Collection? = clazz.getDeclaredAnnotation(Collection::class.java)
        return if (collection != null)
            return collection.name
        else {
            this.logger?.info(this, "视图类没有标记[Collection]注解，无法获取Collection名称。尝试使用View<${clazz.name}>名称解析集合")
            val name = clazz.simpleName.removeSuffix("View").firstCharLowerCase()
            if (!validViewCollection || this.repo.collectionExists(name))
                name
            else {
                throw RuntimeException("找不到名为[$collection]的集合")
            }
        }
    }
}

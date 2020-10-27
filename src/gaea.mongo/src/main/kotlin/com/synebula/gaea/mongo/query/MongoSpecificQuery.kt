package com.synebula.gaea.mongo.query

import com.synebula.gaea.extension.fieldNames
import com.synebula.gaea.extension.firstCharLowerCase
import com.synebula.gaea.log.ILogger
import com.synebula.gaea.mongo.order
import com.synebula.gaea.mongo.select
import com.synebula.gaea.mongo.where
import com.synebula.gaea.mongo.whereId
import com.synebula.gaea.query.ISpecificQuery
import com.synebula.gaea.query.Page
import com.synebula.gaea.query.Params
import com.synebula.gaea.query.annotation.Table
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query

/**
 * 实现IQuery的Mongo查询类
 * @param clazz 查询的对象类
 * @param template MongoRepo对象
 * @param logger 日志组件
 */
open class MongoSpecificQuery<TView>(
    var template: MongoTemplate,
    var clazz: Class<TView>? = null,
    var logger: ILogger? = null
) : ISpecificQuery<TView, String> {

    /**
     * 使用View解析是collection时是否校验存在，默认不校验
     */
    var validViewCollection = false

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
            this.collection(this.clazz)
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
            : this(query, clazz) {
        this.collection = collection
    }


    override fun list(params: Map<String, Any>?): List<TView> {
        this.check()
        return if (this.clazz != null) {
            val fields = this.clazz!!.fieldNames()
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

    override fun paging(param: Params): Page<TView> {
        this.check()
        return if (this.clazz != null) {
            val query = Query()
            val fields = this.clazz!!.fieldNames()
            val result = Page<TView>(param.page, param.size)
            result.total = this.count(param.parameters)
            //如果总数和索引相同，说明该页没有数据，直接跳到上一页
            if (result.total == result.index) {
                param.page -= 1
                result.page -= 1
            }
            query.select(fields.toTypedArray())
            query.where(param.parameters, this.clazz!!)
            query.with(order(param.orders))
            query.skip(param.index).limit(param.size)
            result.data = this.template.find(query, this.clazz!!, this.collection)
            result
        } else Page(1, 10)
    }

    override fun get(id: String): TView? {
        this.check()
        return if (this.clazz != null) {
            val view = this.template.findOne(whereId(id), this.clazz!!, this.collection)
            view
        } else null
    }

    protected fun check() {
        if (this.clazz == null)
            throw RuntimeException("[${this.javaClass.name}] 没有声明查询View的类型")
        if (this._collection.isEmpty())
            this.logger?.warn(this, "查询集合参数[collection]值为空, 尝试使用View<${this.clazz?.name}>名称解析集合")
    }

    /**
     * 获取collection
     */
    protected fun <TView> collection(clazz: Class<TView>?): String {
        if (clazz == null) throw java.lang.RuntimeException("[${this.javaClass}]没有指定查询实体类型[clazz]")
        val collection = clazz.getDeclaredAnnotation(Table::class.java)
        return if (collection != null)
            return collection.name
        else {
            this.logger?.info(this, "视图类没有标记[Collection]注解，无法获取Collection名称。尝试使用View<${clazz.name}>名称解析集合")
            val name = clazz.simpleName.removeSuffix("View").firstCharLowerCase()
            if (!validViewCollection || this.template.collectionExists(name))
                name
            else {
                throw RuntimeException("找不到名为[$collection]的集合")
            }
        }
    }
}

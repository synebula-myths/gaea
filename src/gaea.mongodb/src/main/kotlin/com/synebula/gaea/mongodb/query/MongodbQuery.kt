package com.synebula.gaea.mongodb.query

import com.synebula.gaea.ext.fieldNames
import com.synebula.gaea.ext.firstCharLowerCase
import com.synebula.gaea.mongodb.order
import com.synebula.gaea.mongodb.select
import com.synebula.gaea.mongodb.where
import com.synebula.gaea.mongodb.whereId
import com.synebula.gaea.query.IQuery
import com.synebula.gaea.query.Page
import com.synebula.gaea.query.Params
import com.synebula.gaea.query.Table
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

/**
 * 实现IQuery的Mongodb查询类
 * @param template MongodbRepo对象
 */

open class MongodbQuery<TView, ID>(override var clazz: Class<TView>, var template: MongoTemplate) :
    IQuery<TView, ID> {

    /**
     * 使用View解析是collection时是否校验存在，默认不校验
     */
    var validViewCollection = false

    override fun get(id: ID): TView? {
        return this.template.findOne(whereId(id), clazz, this.collection(clazz))
    }

    override fun list(params: Map<String, String>?): List<TView> {
        val fields = this.fields(clazz)
        val query = Query()
        query.where(params, clazz)
        query.select(fields)
        return this.find(query, clazz)
    }

    override fun count(params: Map<String, String>?): Int {
        val query = Query()
        return this.template.count(query.where(params, clazz), this.collection(clazz)).toInt()
    }

    override fun paging(params: Params): Page<TView> {
        val query = Query()
        val fields = this.fields(clazz)
        val result = Page<TView>(params.page, params.size)
        result.total = this.count(params.parameters)
        //如果总数和索引相同，说明该页没有数据，直接跳到上一页
        if (result.total == result.index) {
            params.page -= 1
            result.page -= 1
        }
        query.select(fields)
        query.where(params.parameters, clazz)
        query.with(order(params.orders))
        query.skip(params.index).limit(params.size)
        result.data = this.find(query, clazz)
        return result
    }

    override fun range(field: String, params: List<Any>): List<TView> {
        return this.find(Query.query(Criteria.where(field).`in`(params)), clazz)
    }

    protected fun find(query: Query, clazz: Class<TView>): List<TView> {
        return this.template.find(query, clazz, this.collection(clazz))
    }

    protected fun fields(clazz: Class<TView>): Array<String> {
        val fields = mutableListOf<String>()
        fields.addAll(clazz.fieldNames())
        var parent = clazz.superclass
        while (parent != Any::class.java) {
            fields.addAll(clazz.superclass.fieldNames())
            parent = parent.superclass
        }
        return fields.toTypedArray()
    }

    /**
     * 获取collection
     */
    fun <TView> collection(clazz: Class<TView>): String {
        val table = clazz.getDeclaredAnnotation(Table::class.java)
        return if (table != null) table.name
        else {
            val name = clazz.simpleName.removeSuffix("View").firstCharLowerCase()
            if (!validViewCollection || this.template.collectionExists(name)) name
            else throw RuntimeException("找不到名为[${clazz.name}]的集合")
        }
    }
}

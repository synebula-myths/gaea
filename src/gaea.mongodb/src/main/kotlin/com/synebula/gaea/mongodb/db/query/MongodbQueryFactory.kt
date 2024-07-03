package com.synebula.gaea.mongodb.db.query

import com.synebula.gaea.db.query.IQuery
import com.synebula.gaea.db.query.IQueryFactory
import org.springframework.data.mongodb.core.MongoTemplate

class MongodbQueryFactory(var template: MongoTemplate) : IQueryFactory {

    /**
     * 创建IQuery接口类型
     */
    override fun createRawQuery(clazz: Class<*>): IQuery<*, *> {
        val constructor = MongodbQuery::class.java.getConstructor(Class::class.java, MongoTemplate::class.java)
        return constructor.newInstance(clazz, this.template)
    }

    /**
     * 创建IQuery接口类型
     */
    override fun <T, I> createQuery(clazz: Class<T>): IQuery<T, I> {
        return MongodbQuery(clazz, template)
    }
}
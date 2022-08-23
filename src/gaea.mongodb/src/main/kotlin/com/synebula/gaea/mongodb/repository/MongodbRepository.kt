package com.synebula.gaea.mongodb.repository

import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.domain.repository.IRepository
import com.synebula.gaea.mongodb.where
import com.synebula.gaea.mongodb.whereId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query

/**
 * 实现[IRepository]的Mongodb仓储类
 * @param repo MongodbRepo对象
 */
open class MongodbRepository<TAggregateRoot : IAggregateRoot<ID>, ID>(
    override var clazz: Class<TAggregateRoot>,
    protected var repo: MongoTemplate
) : IRepository<TAggregateRoot, ID> {

    override fun add(obj: TAggregateRoot) {
        this.repo.save(obj)
    }

    override fun add(list: List<TAggregateRoot>) {
        this.repo.insert(list, clazz)
    }

    override fun remove(id: ID) {
        this.repo.remove(whereId(id), clazz)
    }

    override fun get(id: ID): TAggregateRoot? {
        return this.repo.findOne(whereId(id), clazz)
    }

    override fun update(obj: TAggregateRoot) {
        this.repo.save(obj)
    }

    override fun update(list: List<TAggregateRoot>) {
        this.repo.save(list)
    }

    override fun count(params: Map<String, Any>?): Int {
        val query = Query()
        return this.repo.count(query.where(params, clazz), clazz).toInt()
    }
}

package com.synebula.gaea.mongo.repository

import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.domain.repository.IRepository
import com.synebula.gaea.mongo.where
import com.synebula.gaea.mongo.whereId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query

/**
 * 实现ITypedRepository的Mongodb仓储类
 * @param repo MongodbRepo对象
 */
open class MongodbRepository(private var repo: MongoTemplate) : IRepository {

    override fun <TAggregateRoot : IAggregateRoot<ID>, ID> remove(id: ID, clazz: Class<TAggregateRoot>) {
        this.repo.remove(whereId(id), clazz)
    }

    override fun <TAggregateRoot : IAggregateRoot<ID>, ID> get(
        id: ID,
        clazz: Class<TAggregateRoot>,
    ): TAggregateRoot? {
        return this.repo.findOne(whereId(id), clazz)
    }

    override fun <TAggregateRoot : IAggregateRoot<ID>, ID> update(
        obj: TAggregateRoot,
        clazz: Class<TAggregateRoot>,
    ) {
        this.repo.save(obj)
    }

    override fun <TAggregateRoot : IAggregateRoot<ID>, ID> add(obj: TAggregateRoot, clazz: Class<TAggregateRoot>) {
        this.repo.save(obj)
    }

    override fun <TAggregateRoot : IAggregateRoot<ID>, ID> add(
        obj: List<TAggregateRoot>,
        clazz: Class<TAggregateRoot>,
    ) {
        this.repo.insert(obj, clazz)
    }

    override fun <TAggregateRoot> count(params: Map<String, Any>?, clazz: Class<TAggregateRoot>): Int {
        val query = Query()
        return this.repo.count(query.where(params, clazz), clazz).toInt()
    }
}

package com.synebula.gaea.mongo.repository

import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.domain.repository.IRepository
import com.synebula.gaea.mongo.where
import com.synebula.gaea.mongo.whereId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query

/**
 * 实现ITypedRepository的mongo仓储类
 * @param repo MongoRepo对象
 */
open class MongoRepository(private var repo: MongoTemplate) : IRepository {

    override fun <TAggregateRoot : IAggregateRoot<TKey>, TKey> remove(id: TKey, clazz: Class<TAggregateRoot>) {
        this.repo.remove(whereId(id), clazz)
    }

    override fun <TAggregateRoot : IAggregateRoot<TKey>, TKey> get(
            id: TKey,
            clazz: Class<TAggregateRoot>
    ): TAggregateRoot? {
        return this.repo.findOne(whereId(id), clazz)
    }

    override fun <TAggregateRoot : IAggregateRoot<TKey>, TKey> update(
            obj: TAggregateRoot,
            clazz: Class<TAggregateRoot>
    ) {
        this.repo.save(obj)
    }

    override fun <TAggregateRoot : IAggregateRoot<TKey>, TKey> add(obj: TAggregateRoot, clazz: Class<TAggregateRoot>) {
        this.repo.save(obj)
    }

    override fun <TAggregateRoot : IAggregateRoot<TKey>, TKey> add(obj: List<TAggregateRoot>, clazz: Class<TAggregateRoot>) {
        this.repo.insert(obj, clazz)
    }

    override fun <TAggregateRoot> count(params: Map<String, Any>?, clazz: Class<TAggregateRoot>): Int {
        val query = Query()
        return this.repo.count(query.where(params, clazz), clazz).toInt()
    }
}

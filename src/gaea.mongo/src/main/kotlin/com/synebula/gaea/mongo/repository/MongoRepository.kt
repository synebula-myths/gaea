package com.synebula.gaea.mongo.repository

import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.domain.repository.IRepository
import com.synebula.gaea.mongo.whereId
import org.springframework.data.mongodb.core.MongoTemplate

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
    ): TAggregateRoot {
        return this.repo.findOne(whereId(id), clazz) as TAggregateRoot
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
}

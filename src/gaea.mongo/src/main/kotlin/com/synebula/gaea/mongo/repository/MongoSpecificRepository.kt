package com.synebula.gaea.mongo.repository

import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.domain.repository.ISpecificRepository
import com.synebula.gaea.mongo.whereId
import org.springframework.data.mongodb.core.MongoTemplate

/**
 * 实现IAggregateRoot的mongo仓储类
 * @param repo MongoRepo对象
 */
class MongoSpecificRepository<TAggregateRoot : IAggregateRoot<String>>(private var repo: MongoTemplate) :
    ISpecificRepository<TAggregateRoot, String> {

    /**
     * 仓储的对象类
     */
    override var clazz: Class<TAggregateRoot>? = null

    /**
     * 构造
     * @param clazz 仓储Domain对象
     * @param repo MongoRepo对象
     */
    constructor(clazz: Class<TAggregateRoot>, repo: MongoTemplate) : this(repo) {
        this.clazz = clazz
    }

    override fun add(obj: TAggregateRoot) {
        this.repo.save(obj)
    }

    override fun update(obj: TAggregateRoot) {
        this.repo.save(obj)
    }

    override fun remove(id: String) {
        this.repo.remove(whereId(id), this.clazz!!)
    }

    override fun get(id: String): TAggregateRoot {
        return this.repo.findOne(whereId(id), clazz!!) as TAggregateRoot
    }

    override fun <TAggregateRoot : IAggregateRoot<TKey>, TKey> get(
        id: TKey,
        clazz: Class<TAggregateRoot>
    ): TAggregateRoot {
        return this.repo.findOne(whereId(id.toString()), clazz) as TAggregateRoot
    }
}

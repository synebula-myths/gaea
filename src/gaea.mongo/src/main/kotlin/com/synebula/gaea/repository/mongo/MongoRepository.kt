package com.synebula.gaea.repository.mongo

import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.domain.repository.IRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo

/**
 * 实现IAggregateRoot的mongo仓储类
 * @param repo MongoRepo对象
 */
class MongoRepository<TAggregateRoot : IAggregateRoot<String>>(private var repo: MongoTemplate)
    : IRepository<TAggregateRoot, String> {

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
        this.repo.remove(idQuery(id), this.clazz!!)
    }

    override fun get(id: String): TAggregateRoot {
        return this.repo.findOne(idQuery(id), clazz!!) as TAggregateRoot
    }

    override fun <TAggregateRoot : IAggregateRoot<TKey>, TKey> get(id: TKey, clazz: Class<TAggregateRoot>): TAggregateRoot {
        return this.repo.findOne(idQuery(id.toString()), clazz) as TAggregateRoot
    }

    /**
     * 获取ID查询条件
     *
     * @param id 业务ID
     */
    fun idQuery(id: String): Query = Query(Criteria("_id").isEqualTo(id))
}
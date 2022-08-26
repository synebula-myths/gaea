package com.synebula.gaea.mongodb.repository

import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.domain.repository.IUniversalRepository
import com.synebula.gaea.mongodb.where
import com.synebula.gaea.mongodb.whereId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query

/**
 * 实现ITypedRepository的Mongodb仓储类
 * @param repo MongodbRepo对象
 */
open class MongodbUniversalRepository(private var repo: MongoTemplate) : IUniversalRepository {

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
        root: TAggregateRoot,
        clazz: Class<TAggregateRoot>,
    ) {
        this.repo.save(root)
    }

    /**
     * 更新多个个对象。
     *
     * @param roots 需要更新的对象。
     */
    override fun <TAggregateRoot : IAggregateRoot<ID>, ID> update(
        roots: List<TAggregateRoot>,
        clazz: Class<TAggregateRoot>
    ) {
        this.repo.save(roots)
    }

    override fun <TAggregateRoot : IAggregateRoot<ID>, ID> add(root: TAggregateRoot, clazz: Class<TAggregateRoot>) {
        this.repo.save(root)
    }

    override fun <TAggregateRoot : IAggregateRoot<ID>, ID> add(
        roots: List<TAggregateRoot>,
        clazz: Class<TAggregateRoot>,
    ) {
        this.repo.insert(roots, clazz)
    }

    override fun <TAggregateRoot> count(params: Map<String, String>?, clazz: Class<TAggregateRoot>): Int {
        val query = Query()
        return this.repo.count(query.where(params, clazz), clazz).toInt()
    }
}

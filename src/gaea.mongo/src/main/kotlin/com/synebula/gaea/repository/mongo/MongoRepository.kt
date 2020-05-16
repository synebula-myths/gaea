package com.synebula.gaea.repository.mongo

import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.domain.repository.ITypedRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
abstract class MongoRepository<TAggregateRoot : IAggregateRoot<String>> : ITypedRepository<TAggregateRoot, String> {

    @Autowired
    private lateinit var repo: MongoTemplate

    override fun remove(id: String, clazz: Class<TAggregateRoot>) {
        this.repo.remove(queryId(id), clazz)
    }

    override fun get(id: String, clazz: Class<TAggregateRoot>): TAggregateRoot {
        return this.repo.findOne(queryId(id), clazz) as TAggregateRoot
    }

    override fun update(obj: TAggregateRoot) {
        this.repo.save(obj)
    }

    override fun add(obj: TAggregateRoot) {
        this.repo.save(obj)
    }

    protected fun queryId(id: String): Query = Query(Criteria("_id").isEqualTo(id))
}
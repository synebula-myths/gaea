package com.synebula.gaea.mongodb.db.context

import com.synebula.gaea.db.IEntity
import com.synebula.gaea.db.context.IDbContext
import com.synebula.gaea.mongodb.where
import com.synebula.gaea.mongodb.whereId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query

class MongodbContext(
    protected var template: MongoTemplate
) : IDbContext {

    override fun <TEntity : IEntity<ID>, ID> add(entity: TEntity, clazz: Class<TEntity>) {
        this.template.save(entity)
    }

    override fun <TEntity : IEntity<ID>, ID> add(entities: List<TEntity>, clazz: Class<TEntity>) {
        this.template.insert(entities, clazz)
    }

    override fun <TEntity : IEntity<ID>, ID> remove(id: ID, clazz: Class<TEntity>) {
        this.template.remove(whereId(id), clazz)
    }

    override fun <TEntity : IEntity<ID>, ID> get(id: ID, clazz: Class<TEntity>): TEntity? {
        return this.template.findOne(whereId(id), clazz)
    }

    override fun <TEntity : IEntity<ID>, ID> update(entity: TEntity, clazz: Class<TEntity>) {
        this.template.save(entity)
    }

    override fun <TEntity : IEntity<ID>, ID> update(entities: List<TEntity>, clazz: Class<TEntity>) {
        this.template.save(entities)
    }

    override fun <TEntity : IEntity<ID>, ID> count(params: Map<String, String>?, clazz: Class<TEntity>): Int {
        val query = Query()
        return this.template.count(query.where(params, clazz), clazz).toInt()
    }

    override val isCommitted=true

    override fun commit() {
    }

    override fun rollback() {
    }
}

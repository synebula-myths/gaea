package com.synebula.gaea.repository.mongo

import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.domain.repository.IRepositoryTyped
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo

/**
 * 实现ITypedRepository的mongo仓储类
 * @param repo MongoRepo对象
 */
open class MongoRepositoryTyped(private var repo: MongoTemplate)
  : IRepositoryTyped, IMongoRepository {

  override fun <TAggregateRoot : IAggregateRoot<TKey>, TKey> remove(id: TKey, clazz: Class<TAggregateRoot>) {
    this.repo.remove(idQuery(id), clazz)
  }

  override fun <TAggregateRoot : IAggregateRoot<TKey>, TKey> get(id: TKey, clazz: Class<TAggregateRoot>): TAggregateRoot {
    return this.repo.findOne(idQuery(id), clazz) as TAggregateRoot
  }

  override fun <TAggregateRoot : IAggregateRoot<TKey>, TKey> update(obj: TAggregateRoot, clazz: Class<TAggregateRoot>) {
    this.repo.save(obj)
  }

  override fun <TAggregateRoot : IAggregateRoot<TKey>, TKey> add(obj: TAggregateRoot, clazz: Class<TAggregateRoot>) {
    this.repo.save(obj)
  }


}

package com.synebula.gaea.repository.mongo

import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo

interface IMongoRepository {
  /**
   * 获取ID查询条件
   *
   * @param id 业务ID
   */
  fun <TKey> idQuery(id: TKey): Query = Query(Criteria("_id").isEqualTo(id))
}

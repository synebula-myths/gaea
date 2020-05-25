package com.synebula.gaea.domain.model

import java.util.*

/**
 * 记录聚合根
 * 聚合根外添加了创建和修改的人\时间信息
 */
abstract class AggregateRecord<TKey> : AggregateRoot<TKey>() {
    var creator: String? = null
    var creatorName: String? = null
    var created: Date = Date()
        set(value) {
            field = value
            modified = value
        }

    var modifier: String? = null
    var modifierName: String? = null
    var modified: Date = Date()
}
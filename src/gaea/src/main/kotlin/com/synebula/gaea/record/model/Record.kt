package com.synebula.gaea.record.model

import java.util.*


/**
 * 记录信息
 * 添加了创建和修改的人\时间信息
 */
abstract class Record<ID> {

    //记录增加信息
    var createdBy: String? = null
    var creatorByName: String? = null
    var createdAt: Date = Date()

    //记录修改信息
    var modifiedBy: String? = null
    var modifiedByName: String? = null
    var modifiedAt: Date = Date()
}
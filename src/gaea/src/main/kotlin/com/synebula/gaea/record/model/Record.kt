package com.synebula.gaea.record.model

import java.util.*


/**
 * 记录信息
 * 添加了创建和修改的人\时间信息
 */
abstract class Record<ID> {

    //记录增加信息
    var creator: String? = null
    var creatorName: String? = null
    var createTime: Date = Date()

    //记录修改信息
    var modifier: String? = null
    var modifierName: String? = null
    var modifyTime: Date = Date()
}
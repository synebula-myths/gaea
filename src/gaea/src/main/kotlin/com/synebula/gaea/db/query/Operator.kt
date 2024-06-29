package com.synebula.gaea.db.query

enum class Operator {
    /**
     * 等于
     */
    Eq,

    /**
     * 不等于
     */
    Ne,

    /**
     * 小于
     */
    Lt,

    /**
     * 大于
     */
    Gt,

    /**
     * 小于或等于
     */
    Lte,

    /**
     * 大于或等于
     */
    Gte,

    /**
     * 模糊匹配
     */
    Like,

    /**
     * 范围内
     */
    Range,

    /**
     * 默认查询, 未定义查询方式, 业务人员自己实现查询方式
     */
    Default
}
package com.synebula.gaea.query.type

enum class Operator {
    /**
     * 等于
     */
    eq,

    /**
     * 不等于
     */
    ne,

    /**
     * 小于
     */
    lt,

    /**
     * 大于
     */
    gt,

    /**
     * 小于或等于
     */
    lte,

    /**
     * 大于或等于
     */
    gte,

    /**
     * 模糊匹配
     */
    like,

    /**
     * 默认查询, 未定义查询方式, 业务人员自己实现查询方式
     */
    default
}
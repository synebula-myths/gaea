package com.synebula.gaea.data.message

/**
 * 消息结构
 */
interface IMessage {
    /**
     * 命令载荷, 实际的业务数据
     */
    var message: String

    /**
     * 时间戳。
     */
    var timestamp: Long
}
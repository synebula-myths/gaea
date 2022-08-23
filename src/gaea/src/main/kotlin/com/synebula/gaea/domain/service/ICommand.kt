package com.synebula.gaea.domain.service

/**
 * 命令接口，发给service的命令。
 *
 * @author alex
 * @version 0.1
 * @since 2020-05-15
 */
interface ICommand {

    /**
     * 命令载荷, 实际的业务数据
     */
    var payload: String

    /**
     * 时间戳。
     */
    var timestamp: Long
}

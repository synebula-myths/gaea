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
     * 时间戳。
     */
    var timestamp: Long
}

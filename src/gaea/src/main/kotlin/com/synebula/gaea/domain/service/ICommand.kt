package com.synebula.gaea.domain.service

/**
 * 命令接口，发给service的命令。
 *
 * @author alex
 * @version 0.1
 * @since 2018 18-2-8
 */
interface ICommand {
    /**
     * 时间戳。
     */
    var timestamp: Long
}

package com.synebula.gaea.domain.service

import java.util.*

/**
 * 命令基础实现类，发给service的命令。
 *
 * @author alex
 * @version 0.1
 * @since 2020-05-15
 */
class Command : ICommand {
    override var timestamp = 0L
}

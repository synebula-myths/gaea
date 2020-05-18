package com.synebula.gaea.domain.service

import com.synebula.gaea.data.message.Message

/**
 * class IFlatService
 *
 * @author alex
 * @version 0.1
 * @since 2020-05-15
 */
interface IServiceComplex<TKey, TSecond> {

    fun add(command: ICommand): Message<Pair<TKey, TSecond>>

    fun update(key: TKey, secondary: TSecond, command: ICommand)

    fun remove(key: TKey, secondary: TSecond)
}

package com.synebula.gaea.domain.service

import com.synebula.gaea.data.message.Message

/**
 * class IFlatService
 *
 * @author alex
 * @version 0.1
 * @since 2018 18-2-8
 */
interface IServiceComplex<TKey, TSecond> {

    fun add(command: ICommand): Message<Pair<TKey, TSecond>>

    fun update(key: TKey, secondary: TSecond, command: ICommand)

    fun remove(key: TKey, secondary: TSecond)
}

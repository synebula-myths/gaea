package com.synebula.gaea.domain.service

import com.synebula.gaea.data.message.http.HttpMessage

/**
 * class IFlatService
 *
 * @author alex
 * @version 0.1
 * @since 2018 18-2-8
 */
interface IComplexService<TKey, TSecond> {

    fun add(command: ICommand): HttpMessage<Pair<TKey, TSecond>>

    fun update(key: TKey, secondary: TSecond, command: ICommand)

    fun remove(key: TKey, secondary: TSecond)
}

package com.synebula.gaea.domain.service

import com.synebula.gaea.data.message.DataMessage


/**
 * 继承该接口，表明对象为领域服务。
 * @author alex
 * @version 0.0.1
 * @since 2016年9月18日 下午2:23:15
 */
interface IService<ID> {

    /**
     * 增加对象
     *
     * @param command 增加对象命令
     */
    fun add(command: ICommand): DataMessage<ID>

    /**
     * 增加对象
     *
     * @param commands 增加对象命令列表
     */
    fun add(commands: List<ICommand>)

    /**
     * 更新对象
     *
     * @param id 对象ID
     * @param command 更新对象命令
     */
    fun update(id: ID, command: ICommand)

    /**
     * 批量更新对象
     *
     * @param commands 更新对象命令列表
     */
    fun update(commands: List<ICommand>)

    /**
     * 增加对象
     * @param id 对象ID
     */
    fun remove(id: ID)
}

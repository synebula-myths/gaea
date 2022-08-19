package com.synebula.gaea.domain.service

import com.synebula.gaea.data.message.DataMessage
import com.synebula.gaea.data.serialization.IObjectMapper
import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.domain.repository.IRepository


/**
 * 依赖了IRepository仓储借口的服务实现类 GenericsService
 * 该类依赖仓储接口 @see IGenericsRepository, 需要显式提供聚合根的class对象
 *
 * @param clazz 聚合根类对象
 * @param repository 仓储对象
 * @param mapper 对象转换组件
 * @author alex
 * @version 0.1
 * @since 2020-05-17
 */
open class Service<TRoot : IAggregateRoot<ID>, ID>(
    protected open var clazz: Class<TRoot>,
    protected open var repository: IRepository<TRoot, ID>,
    protected open var mapper: IObjectMapper,
) : IService<ID> {

    /**
     * 增加对象
     *
     * @param command 增加对象命令
     */
    override fun add(command: ICommand): DataMessage<ID> {
        val msg = DataMessage<ID>()
        val root = this.map(command)
        this.repository.add(root)
        msg.data = root.id
        return msg
    }

    /**
     * 增加对象
     *
     * @param commands 增加对象命令列表
     */
    override fun add(commands: List<ICommand>) {
        val roots = commands.map { this.map(it) }
        this.repository.add(roots)
    }

    /**
     * 更新对象
     *
     * @param id 对象ID
     * @param command 更新对象命令
     */
    override fun update(id: ID, command: ICommand) {
        val root = this.map(command)
        root.id = id
        this.repository.update(root)
    }

    /**
     * 批量更新对象
     *
     * @param commands 更新对象命令列表
     */
    override fun update(commands: List<ICommand>) {
        val roots = commands.map { this.map(it) }
        this.repository.update(roots)
    }

    /**
     * 增加对象
     * @param id 对象ID
     */
    override fun remove(id: ID) {
        this.repository.remove(id)
    }

    /**
     * 转换ICommand类型到聚合根类型，默认实现，根据需要进行覆写。
     *
     * @param command 需要转换的命令
     * @return 聚合根
     */
    protected open fun map(command: ICommand): TRoot {
        try {
            return mapper.deserialize(command, this.clazz)
        } catch (ex: Exception) {
            throw RuntimeException("command not match aggregate root", ex)
        }
    }
}

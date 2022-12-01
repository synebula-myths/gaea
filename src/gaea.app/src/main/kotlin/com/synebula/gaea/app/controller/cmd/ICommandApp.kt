package com.synebula.gaea.app.controller.cmd

import com.synebula.gaea.app.controller.IApplication
import com.synebula.gaea.data.message.HttpMessage
import com.synebula.gaea.data.message.Status
import com.synebula.gaea.domain.service.ICommand
import com.synebula.gaea.domain.service.IService
import com.synebula.gaea.spring.aop.annotation.Method
import org.springframework.web.bind.annotation.*

/**
 * 应用类接口，提供向Command服务的接口
 *
 * @author alex
 * @version 0.1
 * @since 2020-05-15
 */
interface ICommandApp<TCommand : ICommand, ID> : IApplication {
    var service: IService<ID>

    @PostMapping
    @Method("添加")
    fun add(@RequestBody command: TCommand): HttpMessage {
        return this.httpMessageFactory.create(service.add(command))
    }

    @PutMapping("/{id:.+}")
    @Method("更新")
    fun update(@PathVariable id: ID, @RequestBody command: TCommand): HttpMessage {
        this.service.update(id, command)
        return this.httpMessageFactory.create()
    }

    @DeleteMapping("/{id:.+}")
    @Method("删除")
    fun remove(@PathVariable id: ID): HttpMessage {
        val msg = this.httpMessageFactory.create()
        try {
            msg.data = this.service.remove(id)
        } catch (ex: IllegalStateException) {
            msg.status = Status.Error
            msg.message = ex.message ?: ""
        }

        return msg
    }
}

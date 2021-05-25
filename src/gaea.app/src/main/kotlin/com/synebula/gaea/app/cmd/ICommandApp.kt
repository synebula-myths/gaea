package com.synebula.gaea.app.cmd

import com.synebula.gaea.app.IApplication
import com.synebula.gaea.app.component.aop.annotation.MethodName
import com.synebula.gaea.app.struct.HttpMessage
import com.synebula.gaea.data.message.Status
import com.synebula.gaea.data.serialization.json.IJsonSerializer
import com.synebula.gaea.domain.service.ICommand
import com.synebula.gaea.domain.service.IService
import org.springframework.web.bind.annotation.*

/**
 * 应用类接口，提供向Command服务的接口
 *
 * @author alex
 * @version 0.1
 * @since 2020-05-15
 */
interface ICommandApp<TCommand : ICommand, TKey> : IApplication {
    var jsonSerializer: IJsonSerializer?

    var service: IService<TKey>


    @PostMapping
    @MethodName("添加")
    fun add(@RequestBody command: TCommand): HttpMessage {
        val msg = HttpMessage(this.service.add(command))
        return msg
    }

    @PutMapping("/{id:.+}")
    @MethodName("更新")
    fun update(@PathVariable id: TKey, @RequestBody command: TCommand): HttpMessage {
        this.service.update(id, command)
        return HttpMessage()
    }


    @DeleteMapping("/{id:.+}")
    @MethodName("删除")
    fun remove(@PathVariable id: TKey): HttpMessage {
        val msg = HttpMessage()
        try {
            msg.data = this.service.remove(id)
        } catch (ex: IllegalStateException) {
            msg.status = Status.Error
            msg.message = ex.message ?: ""
        }

        return msg
    }
}

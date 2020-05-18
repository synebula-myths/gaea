package com.synebula.gaea.app

import com.synebula.gaea.app.component.HttpMessage
import com.synebula.gaea.data.message.Status
import com.synebula.gaea.domain.service.ICommand
import com.synebula.gaea.domain.service.IService
import org.springframework.web.bind.annotation.*

/**
 * 应用类接口，提供向Command服务的接口
 *
 * @author alex
 * @version 0.1
 * @since 2018 18-2-8
 */
interface ICommandApp<TCommand : ICommand, TKey> : IApplication {

    var service: IService<TKey>?

    @PostMapping
    fun add(@RequestBody command: TCommand): HttpMessage {
        return this.throwExecute("${this.name}添加失败") {
            if (this.service != null) {
                val msg = this.service!!.add(command)
                it.load(msg)
            } else {
                it.status = Status.Error
                it.message = "没有对应服务，无法执行该操作"
            }
        }
    }

    @DeleteMapping("/{key:.+}")
    fun remove(@PathVariable key: TKey): HttpMessage {
        return this.throwExecute("${this.name}删除失败") {
            if (this.service != null)
                it.data = this.service!!.remove(key)
            else {
                it.status = Status.Error
                it.message = "没有对应服务，无法执行该操作"
            }
        }
    }

    @PutMapping("/{key:.+}")
    fun update(@PathVariable key: TKey, @RequestBody command: TCommand): HttpMessage {
        return this.throwExecute("${this.name}更新失败") {
            if (this.service != null)
                this.service!!.update(key, command)
            else {
                it.status = Status.Error
                it.message = "没有对应服务，无法执行该操作"
            }
        }
    }
}
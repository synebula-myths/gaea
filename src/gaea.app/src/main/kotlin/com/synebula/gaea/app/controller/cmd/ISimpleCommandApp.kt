package com.synebula.gaea.app.controller.cmd

import com.synebula.gaea.app.controller.IApplication
import com.synebula.gaea.data.message.HttpMessage
import com.synebula.gaea.data.message.Status
import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.record.service.IService
import com.synebula.gaea.spring.aop.annotation.Method
import org.springframework.web.bind.annotation.*

/**
 * 直接使用实体对象提供服务的api
 *
 * @author alex
 * @version 0.1
 * @since 2020-05-15
 */
interface ISimpleCommandApp<TRoot : IAggregateRoot<ID>, ID> : IApplication {
    var service: IService<TRoot, ID>

    @PostMapping
    @Method("添加")
    fun add(@RequestBody entity: TRoot): HttpMessage {
        val id = service.add(entity)
        return this.httpMessageFactory.create(id!!)
    }

    @PutMapping("/{id:.+}")
    @Method("更新")
    fun update(@PathVariable id: ID, @RequestBody entity: TRoot): HttpMessage {
        this.service.update(id, entity)
        return this.httpMessageFactory.create()
    }

    @DeleteMapping("/{id:.+}")
    @Method("删除")
    fun remove(@PathVariable id: ID): HttpMessage {
        val msg = this.httpMessageFactory.create()
        try {
            msg.data = this.service.remove(id)
        } catch (ex: IllegalStateException) {
            msg.status = Status.ERROR
            msg.message = ex.message ?: ""
        }

        return msg
    }
}

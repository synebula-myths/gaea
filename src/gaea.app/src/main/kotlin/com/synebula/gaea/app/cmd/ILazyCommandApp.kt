package com.synebula.gaea.app.cmd

import com.synebula.gaea.app.IApplication
import com.synebula.gaea.app.component.aop.annotation.MethodName
import com.synebula.gaea.app.struct.HttpMessage
import com.synebula.gaea.data.message.Status
import com.synebula.gaea.data.serialization.json.IJsonSerializer
import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.domain.service.ILazyService
import org.springframework.web.bind.annotation.*

/**
 * 直接使用实体对象提供服务的api
 *
 * @author alex
 * @version 0.1
 * @since 2020-05-15
 */
interface ILazyCommandApp<TRoot : IAggregateRoot<TKey>, TKey> : IApplication {
    var jsonSerializer: IJsonSerializer?

    var service: ILazyService<TRoot, TKey>

    @PostMapping
    @MethodName("添加")
    fun add(@RequestBody entity: TRoot): HttpMessage {
        return HttpMessage(service.add(entity))
    }

    @PutMapping("/{id:.+}")
    @MethodName("更新")
    fun update(@PathVariable id: TKey, @RequestBody entity: TRoot): HttpMessage {
        this.service.update(id, entity)
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

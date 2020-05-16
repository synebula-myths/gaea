package com.synebula.gaea.app

import com.synebula.gaea.app.components.HttpMessage
import com.synebula.gaea.data.message.Status
import com.synebula.gaea.query.IQuery
import com.synebula.gaea.query.PagingParam
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

/**
 * 应用类接口，提供向Query服务的接口
 *
 * @author alex
 * @version 0.1
 * @since 2018 18-2-8
 */
interface IQueryApp<TView, TKey> : IApplication {
    /**
     * 查询服务
     */
    var query: IQuery<TView, TKey>?

    @GetMapping("/{key:.+}")
    fun get(@PathVariable key: TKey): HttpMessage {
        return this.safeExecute("${this.name}获取数据失败") {
            if (this.query != null)
                it.data = this.query!!.get(key)
            else {
                it.status = Status.Error
                it.message = "没有对应服务，无法执行该操作"
            }
        }
    }

    @GetMapping
    fun list(@RequestParam parameters: MutableMap<String, Any>): HttpMessage {
        return this.safeExecute("${this.name}获取数据失败") {
            if (this.query != null)
                it.data = this.query!!.list(parameters)
            else {
                it.status = Status.Error
                it.message = "没有对应服务，无法执行该操作"
            }
        }
    }

    @GetMapping("/split/{size}/pages/{page}")
    fun paging(@PathVariable page: Int, @PathVariable size: Int, @RequestParam parameters: MutableMap<String, Any>): HttpMessage {
        return this.safeExecute("${this.name}获取分页数据失败") {
            if (this.query != null) {
                val params = PagingParam(page, size)
                params.parameters = parameters
                it.data = this.query!!.paging(params)
            } else {
                it.status = Status.Error
                it.message = "没有对应服务，无法执行该操作"
            }
        }
    }

}

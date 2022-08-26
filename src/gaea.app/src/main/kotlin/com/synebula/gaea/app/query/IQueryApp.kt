package com.synebula.gaea.app.query

import com.synebula.gaea.app.IApplication
import com.synebula.gaea.data.message.HttpMessage
import com.synebula.gaea.query.IQuery
import com.synebula.gaea.query.Params
import com.synebula.gaea.spring.aop.annotation.Method
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

interface IQueryApp<TView, ID> : IApplication {
    /**
     * 查询服务
     */
    var query: IQuery<TView, ID>

    @Method("获取数据")
    @GetMapping("/{id:.+}")
    fun get(@PathVariable id: ID): HttpMessage {
        val data = this.query.get(id)
        val msg = HttpMessage()
        msg.data = data
        return msg
    }

    @Method("获取列表数据")
    @GetMapping
    fun list(@RequestParam params: LinkedHashMap<String, String>): HttpMessage {
        val data = this.query.list(params)
        return HttpMessage(data)
    }

    @Method("获取分页数据")
    @GetMapping("/size/{size}/pages/{page}")
    fun paging(
        @PathVariable size: Int,
        @PathVariable page: Int,
        @RequestParam parameters: LinkedHashMap<String, String>
    ): HttpMessage {
        val params = Params(page, size, parameters)
        val data = this.query.paging(params)
        return HttpMessage(data)
    }
}
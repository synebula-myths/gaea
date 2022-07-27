package com.synebula.gaea.app.query

import com.synebula.gaea.app.IApplication
import com.synebula.gaea.app.component.aop.annotation.Method
import com.synebula.gaea.app.struct.HttpMessage
import com.synebula.gaea.query.IQuery
import com.synebula.gaea.query.Params
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

interface IQueryApp<TView, ID> : IApplication {
    /**
     * 查询服务
     */
    var query: IQuery

    /**
     * 查询的View类型
     */
    var clazz: Class<TView>

    @Method("获取数据")
    @GetMapping("/{id:.+}")
    fun get(@PathVariable id: ID): HttpMessage {
        val data = this.query.get(id, clazz)
        val msg = HttpMessage()
        msg.data = data
        return msg
    }

    @Method("获取列表数据")
    @GetMapping
    fun list(@RequestParam params: LinkedHashMap<String, Any>): HttpMessage {
        val data = this.query.list(params, clazz)
        return HttpMessage(data)
    }

    @Method("获取分页数据")
    @GetMapping("/segments/{size}/pages/{page}")
    fun paging(
        @PathVariable size: Int,
        @PathVariable page: Int,
        @RequestParam parameters: LinkedHashMap<String, Any>
    ): HttpMessage {
        val params = Params(page, size, parameters)
        val data = this.query.paging(params, clazz)
        return HttpMessage(data)
    }
}
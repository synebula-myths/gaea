package com.synebula.gaea.app.query

import com.synebula.gaea.app.IApplication
import com.synebula.gaea.app.struct.HttpMessage
import com.synebula.gaea.app.component.aop.annotation.MethodName
import com.synebula.gaea.data.message.Status
import com.synebula.gaea.query.IQuery
import com.synebula.gaea.query.Params
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

interface IQueryApp<TView, TKey> : IApplication {
    /**
     * 查询服务
     */
    var query: IQuery?

    /**
     * 查询的View类型
     */
    var clazz: Class<TView>

    @MethodName("获取数据")
    @GetMapping("/{id:.+}")
    fun get(@PathVariable id: TKey): HttpMessage {
        return this.doQuery {
            this.query!!.get(id, clazz)
        }
    }

    @MethodName("获取列表数据")
    @GetMapping
    fun list(@RequestParam params: LinkedHashMap<String, Any>): HttpMessage {
        return this.doQuery {
            this.query!!.list(params, clazz)
        }
    }

    @MethodName("获取分页数据")
    @GetMapping("/segments/{size}/pages/{page}")
    fun paging(
        @PathVariable size: Int,
        @PathVariable page: Int,
        @RequestParam parameters: LinkedHashMap<String, Any>
    ): HttpMessage {
        return this.doQuery {
            val data = Params(page, size, parameters)
            this.query!!.paging(data, clazz)
        }
    }


    /**
     * 抽取查询业务判断功能
     *
     * @param biz 业务执行逻辑
     */
    fun doQuery(biz: (() -> Any?)): HttpMessage {
        val msg = HttpMessage()
        if (this.query != null) {
            msg.data = biz()
        } else {
            msg.status = Status.Error
            msg.message = "没有对应服务，无法执行该操作"
        }
        return msg
    }
}
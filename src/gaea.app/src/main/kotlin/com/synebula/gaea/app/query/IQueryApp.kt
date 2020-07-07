package com.synebula.gaea.app.query

import com.synebula.gaea.app.IApplication
import com.synebula.gaea.app.component.HttpMessage
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

    @GetMapping("/{key:.+}")
    fun get(@PathVariable key: TKey): HttpMessage {
        return this.doQuery("获取${this.name}数据失败") {
            this.query!!.get(key, clazz)
        }
    }

    @GetMapping
    fun list(@RequestParam params: MutableMap<String, Any>): HttpMessage {
        return this.doQuery("获取${this.name}列表数据失败") {
            this.query!!.list(params, clazz)
        }
    }

    @GetMapping("/segments/{size}/pages/{page}")
    fun paging(
        @PathVariable size: Int,
        @PathVariable page: Int,
        @RequestParam parameters: MutableMap<String, Any>
    ): HttpMessage {
        return this.doQuery("获取${this.name}分页数据[条数:$size,页码:$page]失败") {
            val data = Params(page, size)
            data.parameters = parameters
            this.query!!.paging(data, clazz)
        }
    }


    /**
     * 抽取查询业务判断功能
     *
     * @param error 错误消息
     * @param biz 业务执行逻辑
     */
    fun doQuery(error: String, biz: (() -> Any?)): HttpMessage {
        return this.safeExecute(error) {
            if (this.query != null) {
                it.data = biz()
            } else {
                it.status = Status.Error
                it.message = "没有对应服务，无法执行该操作"
            }
        }
    }
}
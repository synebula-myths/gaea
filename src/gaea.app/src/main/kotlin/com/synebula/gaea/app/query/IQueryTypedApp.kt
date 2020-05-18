package com.synebula.gaea.app.query

import com.synebula.gaea.app.IApplication
import com.synebula.gaea.app.component.HttpMessage
import com.synebula.gaea.data.message.Status
import com.synebula.gaea.query.IQueryTyped
import com.synebula.gaea.query.PagingParam
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

/**
 * 应用类接口，提供实现Query服务的接口.
 * 依赖查询接口 @see IQueryTyped
 *
 * @author alex
 * @version 0.1
 * @since 2020-05-15
 */
interface IQueryTypedApp<TView, TKey> : IApplication {
    /**
     * 查询服务
     */
    var query: IQueryTyped?

    /**
     * 查询的View类型
     */
    var viewClass: Class<TView>

    @GetMapping("/{key:.+}")
    fun get(@PathVariable key: TKey): HttpMessage {
        return this.safeExecute("获取${this.name}数据失败") {
            if (this.query != null)
                it.data = this.query!!.get(key, viewClass)
            else {
                it.status = Status.Error
                it.message = "没有对应服务，无法执行该操作"
            }
        }
    }

    @GetMapping
    fun list(@RequestParam parameters: MutableMap<String, Any>): HttpMessage {
        return this.safeExecute("获取${this.name}列表数据失败") {
            if (this.query != null)
                it.data = this.query!!.list(parameters, viewClass)
            else {
                it.status = Status.Error
                it.message = "没有对应服务，无法执行该操作"
            }
        }
    }

    @GetMapping("/split/{size}/pages/{page}")
    fun paging(@PathVariable page: Int, @PathVariable size: Int, @RequestParam parameters: MutableMap<String, Any>): HttpMessage {
        return this.safeExecute("获取${this.name}分页数据[条数:$size,页码:$page]失败") {
            if (this.query != null) {
                val params = PagingParam(page, size)
                params.parameters = parameters
                it.data = this.query!!.paging(params, viewClass)
            } else {
                it.status = Status.Error
                it.message = "没有对应服务，无法执行该操作"
            }
        }
    }

}

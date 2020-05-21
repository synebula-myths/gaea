package com.synebula.gaea.app.query

import com.synebula.gaea.app.IApplication
import com.synebula.gaea.app.component.HttpMessage
import com.synebula.gaea.data.message.Status
import com.synebula.gaea.query.IQueryTyped
import com.synebula.gaea.query.PagingParam

/**
 * 应用类接口，提供实现Query服务的接口.
 * 依赖查询接口 @see IQueryTyped
 *
 * @author alex
 * @version 0.1
 * @since 2020-05-15
 */
interface IQueryTypedApp<TView, TKey> : IApplication, IQueryApp<TView, TKey> {
    /**
     * 查询服务
     */
    var query: IQueryTyped?

    /**
     * 查询的View类型
     */
    var viewClass: Class<TView>

    override fun doGet(key: TKey): HttpMessage {
        return this.doQuery("获取${this.name}数据失败") {
            this.query!!.get(key, viewClass)
        }
    }

    override fun doList(params: Map<String, Any>): HttpMessage {
        return this.doQuery("获取${this.name}列表数据失败") {
            this.query!!.list(params, viewClass)
        }
    }

    override fun doPaging(size: Int, page: Int, params: MutableMap<String, Any>): HttpMessage {
        return this.doQuery("获取${this.name}分页数据[条数:$size,页码:$page]失败") {
            val data = PagingParam(page, size)
            data.parameters = params
            this.query!!.paging(data, viewClass)
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
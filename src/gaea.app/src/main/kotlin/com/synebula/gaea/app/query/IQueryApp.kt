package com.synebula.gaea.app.query

import com.synebula.gaea.app.IApplication
import com.synebula.gaea.app.component.HttpMessage
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

interface IQueryApp<TView, TKey> : IApplication {

    @GetMapping("/{key:.+}")
    fun get(@PathVariable key: TKey): HttpMessage {
        return this.doGet(key)
    }

    @GetMapping
    fun list(@RequestParam params: MutableMap<String, Any>): HttpMessage {
        return this.doList(params)
    }

    @GetMapping("/segments/{size}/pages/{page}")
    fun paging(@PathVariable size: Int, @PathVariable page: Int, @RequestParam parameters: MutableMap<String, Any>): HttpMessage {
        return this.doPaging(size, page, parameters)
    }


    /**
     * 实际查询单条数据逻辑
     *
     * @param key 数据的主键
     */
    fun doGet(key: TKey): HttpMessage

    /**
     * 实际查询列表逻辑
     *
     * @param params 查询参数
     */
    fun doList(params: Map<String, Any>): HttpMessage

    /**
     * 实际分页逻辑
     *
     * @param size 单页数量
     * @param page 分页页码
     * @param params 查询参数
     */
    fun doPaging(size: Int, page: Int, params: MutableMap<String, Any>): HttpMessage


}
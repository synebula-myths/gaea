package com.synebula.gaea.app

import com.synebula.gaea.data.message.Message


interface ILogin {
    /**
     * 定义登录方法。
     *
     * @param name 登录名
     * @param password 登录密码
     * @return StatusMessage, data 内容为 map 其中 key account中存储用户账户名称
     */
    fun login(name: String, password: String): Message<Any>
}

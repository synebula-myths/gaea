package com.synebula.gaea.app

import org.springframework.http.HttpMessage

/**
 * 用户登入登出接口定义
 */
interface ISignInOut {
    /**
     * 定义登录方法。
     *
     * @param name 登录名
     * @param password 登录密码
     * @return StatusMessage, data 内容为 map 其中 key account中存储用户账户名称
     */
    fun signIn(name: String, password: String): HttpMessage

    /**
     * 登出
     *
     * @param user 登出的用户
     */
    fun signOut(user: String): HttpMessage
}

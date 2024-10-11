package com.synebula.gaea.app.security.session

/**
 * 登陆用户会话信息
 *
 * @param uid 用户id
 * @param user 用户信息
 */
class UserSession(var uid: String, var user: Any) {

    /**
     * 登录IP
     */
    var ip = ""

    /**
     * 访问的url
     */
    var url = ""

    /**
     * 当前token
     */
    var token = ""

    /**
     * 获取指定类型的用户信息
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> user(): T {
        return user as T
    }
}
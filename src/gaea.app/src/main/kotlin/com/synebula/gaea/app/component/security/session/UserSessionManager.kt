package com.synebula.gaea.app.component.security.session

import com.synebula.gaea.app.component.cache.Cache
import com.synebula.gaea.ext.toMd5
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class UserSessionManager {

    @Value("\${spring.allow-multi-sign}")
    private var allowMultiSign = "true"

    /**
     * 用户id加盐, (尽量)避免泄漏真是用户id
     */
    private val salt = "68d84e0d7b9e"

    /**
     * token分割符
     */
    private val delimiter = "-"

    // 用户的实际信息
    var userSessionCache = Cache<String, UserSession>(7 * 24 * 60 * 60)

    /**
     * 用户登入方法
     */
    fun signIn(uid: String, user: Any): String {
        val session = UserSession(uid, user)
        val id = "$uid$delimiter$salt".toMd5()
        if (allowMultiSign != true.toString()) {
            if (userSessionCache.asMap().keys.any { k -> k.split(delimiter).last() == id })
                throw UnsupportedOperationException("用户已在另一客户端登录, 请退出后重新尝试登录")
        }
        val token = "${UUID.randomUUID()}$delimiter${id}"
        session.token = token
        userSessionCache.add(token, session)
        return token
    }

    fun signOut(token: String) {
        this.userSessionCache.remove(token)
    }

    fun userSession(token: String): UserSession? {
        return userSessionCache[token]
    }
}
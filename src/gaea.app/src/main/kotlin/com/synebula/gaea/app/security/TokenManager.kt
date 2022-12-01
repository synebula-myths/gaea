package com.synebula.gaea.app.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.TokenExpiredException
import com.google.gson.Gson
import com.synebula.gaea.log.ILogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class TokenManager {
    @Autowired
    private lateinit var logger: ILogger

    @Autowired
    private lateinit var gson: Gson

    @Value("\${jwt.secret:}")
    val secret: String = ""

    /**
     * 短期有效期, 默认一天。单位分钟
     */
    @Value("\${jwt.expire.normal:${24 * 60}}")
    private val normalExpire = ""

    /**
     * 长期有效期, 默认一年。单位分钟
     */
    @Value("\${jwt.expire.remember:${365 * 24 * 60}}")
    private val rememberExpire = ""

    /**
     * 放到header的名称
     */
    val header = "token"

    /**
     * 业务载荷名称
     */
    val payload = "user"

    /**
     * 生成签名,5min后过期
     *
     * @param user 用户
     * @return 加密的token
     */
    fun sign(user: Any, remember: Boolean = false): String {
        return this.sign(gson.toJson(user), remember)
    }

    /**
     * 生成签名,5min后过期
     *
     * @param userJson 用户 Json
     * @return 加密的token
     */
    fun sign(userJson: String, remember: Boolean = false): String {
        val milli = this.expireMilliseconds(if (remember) rememberExpire.toLong() else normalExpire.toLong())
        val date = Date(System.currentTimeMillis() + milli)
        val algorithm: Algorithm = Algorithm.HMAC256(secret)
        // 附带username信息
        return JWT.create()
            .withClaim(this.payload, userJson)
            .withIssuedAt(Date())
            .withExpiresAt(date)
            .sign(algorithm)
    }

    /**
     * 校验token是否正确
     *
     * @param token  密钥
     * @param func  自定义验证方法
     */
    fun verifyTime(token: String, func: (total: Long, remain: Long) -> Unit) {
        try {
            val now = Date()
            val jwt = JWT.decode(token)
            val total = jwt.expiresAt.time - jwt.issuedAt.time //总时间
            val remain = jwt.expiresAt.time - now.time //剩余的时间
            func(total, remain)
        } catch (ex: Exception) {
            this.logger.error(this, ex, "解析token出错")
            throw ex
        }
    }

    /**
     * 校验token是否正确
     *
     * @param token  密钥
     * @return 是否正确
     */
    fun <T> verify(token: String, clazz: Class<T>): T? {
        return try {
            val algorithm = Algorithm.HMAC256(secret)
            val result = JWT.require(algorithm).build().verify(token)
            val json = result.getClaim(this.payload).asString()
            gson.fromJson(json, clazz)
        } catch (ex: TokenExpiredException) {
            null
        } catch (ex: Exception) {
            this.logger.error(this, ex, "解析token出错")
            throw ex
        }
    }

    /**
     * 校验token是否正确
     *
     * @param token  密钥
     * @return 是否正确
     */
    fun verify(token: String): String {
        return try {
            val algorithm = Algorithm.HMAC256(secret)
            val result = JWT.require(algorithm).build().verify(token)
            result.getClaim(this.payload).asString()
        } catch (ex: TokenExpiredException) {
            ""
        } catch (ex: Exception) {
            this.logger.error(this, ex, "解析token出错")
            throw ex
        }
    }

    /**
     * 获取超时毫秒
     * @param minutes 分钟数
     */
    private fun expireMilliseconds(minutes: Long): Long {
        return minutes * 60 * 1000
    }
}


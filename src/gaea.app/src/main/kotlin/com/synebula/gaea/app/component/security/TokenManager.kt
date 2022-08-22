package com.synebula.gaea.app.component.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.google.gson.Gson
import com.synebula.gaea.app.struct.exception.TokenCloseExpireException
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
     * @return 是否正确
     */
    fun <T> verify(token: String, clazz: Class<T>): T? {
        try {
            val now = Date()
            val algorithm = Algorithm.HMAC256(secret)
            val jwt = JWT.decode(token)
            val remain = jwt.expiresAt.time - now.time //剩余的时间
            val total = jwt.expiresAt.time - jwt.issuedAt.time //总时间
            if (remain > 0 && 1.0 * remain / total <= 0.3) //存活时间少于总时间的1/3重新下发
                throw TokenCloseExpireException("", JWT.decode(token).getClaim("user").asString())

            val result = JWT.require(algorithm).build().verify(token)
            val json = result.getClaim(this.payload).asString()
            return gson.fromJson(json, clazz)
        } catch (ex: Exception) {
            this.logger.debug(this, ex, "解析token出错")
            throw  ex
        }
    }

    /**
     * 校验token是否正确
     *
     * @param token  密钥
     * @return 是否正确
     */
    fun verify(token: String): String {
        try {
            val now = Date()
            val algorithm = Algorithm.HMAC256(secret)
            val jwt = JWT.decode(token)
            val remain = jwt.expiresAt.time - now.time //剩余的时间
            val total = jwt.expiresAt.time - jwt.issuedAt.time //总时间
            if (remain > 0 && 1.0 * remain / total <= 0.3) //存活时间少于总时间的1/3重新下发
                throw TokenCloseExpireException("", JWT.decode(token).getClaim("user").asString())

            val result = JWT.require(algorithm).build().verify(token)
            return result.getClaim(payload).asString()
        } catch (ex: Exception) {
            this.logger.debug(this, ex, "解析token出错")
            throw  ex
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


package com.synebula.gaea.app.component.security

import com.synebula.gaea.app.component.security.session.UserSessionManager
import com.synebula.gaea.data.message.HttpMessageFactory
import com.synebula.gaea.data.message.Status
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * 登录成功后 走此类进行鉴权操作
 *
 * @param httpMessageFactory HttpMessage 构建器
 * @param userSessionManager 用户信息缓存
 * @param storage 存储识别信息存储方式 token/cookie
 */
class WebAuthorization(
    var httpMessageFactory: HttpMessageFactory,
    var userSessionManager: UserSessionManager,
    var storage: String = "token"
) : OncePerRequestFilter() {

    /**
     *  token 在 header 中的 key
     */
    private val tokenKey = "token"


    /**
     * 在过滤之前和之后执行的事件
     */
    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val identity = if (this.storage == "cookie") {
            request.cookies.find { it.name == tokenKey }?.value ?: ""
        } else {
            request.getHeader(tokenKey) ?: ""
        }
        val user = this.userSessionManager.userSession(identity)
        if (user != null) {
            user.ip = request.remoteAddr
            user.url = request.requestURI
            val authentication = UsernamePasswordAuthenticationToken(user, null, null)
            SecurityContextHolder.getContext().authentication = authentication
            chain.doFilter(request, response)
        } else {
            response.status = Status.Success
            response.characterEncoding = "utf-8"
            response.contentType = "text/javascript;charset=utf-8"
            response.writer.print(
                this.httpMessageFactory.create(
                    Status.Unauthorized,
                    "登录信息失效, 请重新登录"
                )
            )
            response.flushBuffer()
        }
    }
}

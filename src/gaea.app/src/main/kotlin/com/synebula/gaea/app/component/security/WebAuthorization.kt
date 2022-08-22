package com.synebula.gaea.app.component.security

import com.synebula.gaea.app.struct.HttpMessage
import com.synebula.gaea.app.struct.exception.TokenCloseExpireException
import com.synebula.gaea.data.message.Status
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * 登录成功后 走此类进行鉴权操作
 */
class WebAuthorization(authenticationManager: AuthenticationManager, var tokenManager: TokenManager) :
    BasicAuthenticationFilter(authenticationManager) {

    /**
     * 在过滤之前和之后执行的事件
     */
    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val token = request.getHeader(tokenManager.header)
        try {
            val user = tokenManager.verify(token)
            val authentication =
                UsernamePasswordAuthenticationToken(user, null, null)
            SecurityContextHolder.getContext().authentication = authentication
            super.doFilterInternal(request, response, chain)
        } catch (ex: TokenCloseExpireException) {
            response.status = Status.Success
            response.characterEncoding = "utf-8"
            response.contentType = "text/javascript;charset=utf-8"
            response.writer.print(HttpMessage(Status.Reauthorize, tokenManager.sign(ex.payload), "重新下发认证消息"))
            response.flushBuffer()
        }
    }
}

package com.synebula.gaea.app.component.security

import com.synebula.gaea.app.struct.HttpMessage
import com.synebula.gaea.data.message.Status
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.stereotype.Component
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Component
@EnableWebSecurity
class WebSecurity : WebSecurityConfigurerAdapter() {
    @Autowired
    lateinit var tokenManager: TokenManager

    /**
     * 安全配置
     */
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        // 跨域共享
        http.cors()
            .and().csrf().disable() // 跨域伪造请求限制无效
            .authorizeRequests()
            .anyRequest().authenticated()// 资源任何人都可访问
            .and()
            .addFilter(WebAuthorization(authenticationManager(), tokenManager))// 添加JWT鉴权拦截器
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 设置Session的创建策略为：Spring Security永不创建HttpSession 不使用HttpSession来获取SecurityContext
            .and()
            .exceptionHandling()
            .authenticationEntryPoint { _, response, _ ->
                response.status = Status.Success
                response.characterEncoding = "utf-8"
                response.contentType = "text/javascript;charset=utf-8"
                response.writer.print(HttpMessage(Status.Unauthorized, "用户未登录，请重新登录后尝试！"))
            }
    }

    @Throws(Exception::class)
    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers("/sign/**")
    }

    /**
     * 跨域配置
     * @return 基于URL的跨域配置信息
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        // 注册跨域配置
        source.registerCorsConfiguration("/**", CorsConfiguration().applyPermitDefaultValues())
        return source
    }
}

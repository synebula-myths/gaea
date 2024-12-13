package com.synebula.gaea.app.security

import com.synebula.gaea.app.security.session.UserSessionManager
import com.synebula.gaea.data.message.HttpMessageFactory
import com.synebula.gaea.data.message.Status
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity
class WebSecurity {
    @Autowired
    lateinit var userSessionManager: UserSessionManager

    @Autowired
    lateinit var httpMessageFactory: HttpMessageFactory

    @Value("\${spring.sign-in-url}")
    var signInPath = "/sign/in"

    /**
     * 安全配置
     */
    @Bean
    @Throws(Exception::class)
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity.cors(Customizer.withDefaults())
            .csrf { it.disable() } // 跨域伪造请求限制无效
            // 设置Session的创建策略为：Spring Security永不创建HttpSession 不使用HttpSession来获取SecurityContext
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            // 除了登录接口其他资源都必须登录访问
            .authorizeHttpRequests { it.requestMatchers(this.signInPath).permitAll().anyRequest().authenticated() }
            // 添加鉴权拦截器
            .addFilterBefore(
                WebAuthorization(httpMessageFactory, userSessionManager),
                UsernamePasswordAuthenticationFilter::class.java
            ).exceptionHandling {
                it.authenticationEntryPoint { _, response, _ ->
                    response.status = Status.SUCCESS
                    response.characterEncoding = "utf-8"
                    response.contentType = "text/javascript;charset=utf-8"
                    response.writer.print(
                        this.httpMessageFactory.create(
                            Status.FORBIDDEN, "权限不足，请重新登录后尝试！"
                        )
                    )
                }
            }

        return httpSecurity.build()
    }

    @Bean
    @Throws(Exception::class)
    fun ignoringCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web -> web.ignoring().requestMatchers(this.signInPath) }
    }

    /**
     * 跨域配置
     * @return 基于URL的跨域配置信息
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf("*")
        configuration.allowedHeaders = listOf("*")
        // 如果所有的属性不全部配置，一定要执行该方法
        configuration.applyPermitDefaultValues()
        val source = UrlBasedCorsConfigurationSource()
        // 注册跨域配置
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}

package com.synebula.gaea.app.component.aop

import com.synebula.gaea.app.component.HttpMessage
import com.synebula.gaea.app.component.aop.annotation.SafeExec
import com.synebula.gaea.data.message.Status
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.*
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.*


@Aspect
@Component
class AppAspect {
    @Pointcut("within(com.synebula..app.*)")
    fun log() {
    }

    //后置异常通知
    @AfterThrowing("log()")
    fun throws(point: JoinPoint): Any {
        val clazz = point.signature.declaringType
        println("${clazz.name} - ${point.signature.name}异常：${point.signature.name}")
        return "error"
    }

    //后置最终通知,final增强，不管是抛出异常或者正常退出都会执行
    @After("log()")
    fun after(point: JoinPoint) {
        println("方法最后执行.....")
    }

    //环绕通知,环绕增强，相当于MethodInterceptor
    @Around("log()")
    fun around(point: ProceedingJoinPoint): Any? {
        val clazz = point.signature.declaringType
        val func = clazz.methods.find {
            it.name == point.signature.name
        }!!
        val clazzAnnotations = clazz.annotations
        val funcAnnotations = func.annotations ?: arrayOf()

        val attributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes
        val request = attributes.request
        // 记录下请求内容
        println("URL : " + request.requestURL.toString())
        println("HTTP_METHOD : " + request.method)
        println("IP : " + request.remoteAddr)
        println("CLASS_METHOD : " + clazz.name + "." + point.signature.name)
        println("ARGS : " + Arrays.toString(point.args))

        return try {
            val res = point.proceed()
            res
        } catch (ex: Throwable) {
            val msg = HttpMessage(Status.Success)
            for (item in funcAnnotations) {
                if (item.annotationClass == SafeExec::javaClass) {
                }
            }
            println("方法${point.signature}异常: $msg - ${ex.message}")
            return "error"
        }
    }
}
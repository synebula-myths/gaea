package com.synebula.gaea.app.component.aop

import com.synebula.gaea.app.component.HttpMessage
import com.synebula.gaea.app.component.aop.annotation.ExceptionMessage
import com.synebula.gaea.app.component.aop.annotation.Handler
import com.synebula.gaea.app.component.aop.annotation.ModuleName
import com.synebula.gaea.data.message.Status
import com.synebula.gaea.log.ILogger
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.*
import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import com.fasterxml.jackson.databind.ObjectMapper


@Aspect
@Component
class AppAspect {
    private val mapper = ObjectMapper()

    @Autowired
    lateinit var logger: ILogger

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Pointcut("within(com.synebula..app.*)")
    fun log() {
    }

    /**
     * 后置异常通知
     */
    @AfterThrowing("log()", throwing = "ex")
    fun throws(point: JoinPoint, ex: Throwable) {
        val clazz = point.signature.declaringType
        logger.error(
            ex,
            "${clazz.name}.${point.signature.name} exception：${ex.message}， args：${mapper.writeValueAsString(point.args)}"
        )
    }

    /**
     * 环绕通知,环绕增强，相当于MethodInterceptor
     */
    @Around("log()")
    fun around(point: ProceedingJoinPoint): Any? {
        val clazz = point.signature.declaringType
        val func = clazz.methods.find {
            it.name == point.signature.name
        }!!
        val funcAnnotations = func.annotations ?: arrayOf()

        var exceptionMessage = func.name
        //遍历方法注解
        for (funcAnnotation in funcAnnotations) {
            val annotations = funcAnnotation.annotationClass.annotations

            //尝试寻找方法注解的处理类
            val handler = annotations.find { it is Handler }
            if (handler != null && handler is Handler) {
                val handleClazz = applicationContext.getBean(handler.value.java)
                handleClazz.handle(clazz, func, point.args)
            }
            if (funcAnnotation is ExceptionMessage)
                exceptionMessage = funcAnnotation.message
        }

        return try {
            val res = point.proceed()
            res
        } catch (ex: Throwable) {
            //找到类的模块名称，否则使用类名
            var moduleName = clazz.name
            val name = clazz.annotations.find { it is ModuleName }
            if (name != null && name is ModuleName) {
                moduleName = name.value
            }
            val message = "$moduleName - $exceptionMessage"
            logger.error(ex, "$message, args: ${mapper.writeValueAsString(point.args)}")
            return HttpMessage(Status.Error, message)
        }
    }
}
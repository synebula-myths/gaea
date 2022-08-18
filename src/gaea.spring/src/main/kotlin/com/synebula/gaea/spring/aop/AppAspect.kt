package com.synebula.gaea.spring.aop

import com.google.gson.Gson
import com.synebula.gaea.data.message.DataMessage
import com.synebula.gaea.data.message.Status
import com.synebula.gaea.exception.NoticeUserException
import com.synebula.gaea.log.ILogger
import com.synebula.gaea.spring.aop.annotation.Handler
import com.synebula.gaea.spring.aop.annotation.Method
import com.synebula.gaea.spring.aop.annotation.Module
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.core.DefaultParameterNameDiscoverer

abstract class AppAspect {
    private var parameterNameDiscoverer = DefaultParameterNameDiscoverer()

    private val gson = Gson()

    @Autowired
    lateinit var logger: ILogger

    @Autowired
    lateinit var applicationContext: ApplicationContext

    /**
     * 定义切面的方法
     */
    abstract fun func()


    /**
     * 环绕通知,环绕增强，相当于MethodInterceptor
     */
    @Around("func()")
    fun around(point: ProceedingJoinPoint): Any? {
        val clazz = point.`this`.javaClass //获取实际对象的类型避免获取到父类
        val func = point.signature.declaringType.methods.find {
            it.name == point.signature.name
        }!!//获取声明类型中的方法信息
        val funcAnnotations = func.annotations ?: arrayOf()

        var funcName = func.name
        //遍历方法注解
        for (funcAnnotation in funcAnnotations) {
            val annotations = funcAnnotation.annotationClass.annotations

            //尝试寻找方法注解的处理类
            val handler = annotations.find { it is Handler }
            if (handler != null && handler is Handler) {
                val handleClazz = applicationContext.getBean(handler.value.java)
                handleClazz.handle(clazz, func, point.args)
            }
            if (funcAnnotation is Method)
                funcName = funcAnnotation.name
        }

        return try {
            point.proceed()
        } catch (ex: Throwable) {
            //找到类的模块名称，否则使用类名
            var moduleName = clazz.name
            val module = clazz.annotations.find { it is Module }
            if (module != null && module is Module) {
                moduleName = module.name
            }
            var message = "$moduleName - $funcName 异常"
            message = if (ex is NoticeUserException) {
                "$message: ${ex.message}"
            } else {
                "$message。"
            }
            logger.error(
                ex,
                "$message。Method args ${
                    parameterNameDiscoverer.getParameterNames(func)?.contentToString()
                } values is ${
                    gson.toJson(point.args)
                }"
            )
            return gson.toJson(DataMessage<Any>(Status.Error, message))
        }
    }
}
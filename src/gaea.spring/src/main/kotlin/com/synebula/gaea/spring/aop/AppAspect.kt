package com.synebula.gaea.spring.aop

import com.google.gson.Gson
import com.synebula.gaea.data.message.HttpMessageFactory
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

    @Autowired
    lateinit var httpMessageFactory: HttpMessageFactory

    /**
     * 定义切面的方法
     */
    abstract fun func()


    /**
     * 环绕通知,环绕增强，相当于MethodInterceptor
     */
    @Around("func()")
    fun around(point: ProceedingJoinPoint): Any? {
        val func = point.signature.declaringType.methods.find {
            it.name == point.signature.name
        }!!//获取声明类型中的方法信息
        val funcAnnotations = func.annotations ?: arrayOf()

        var funcName = func.name
        //遍历方法注解
        for (funcAnnotation in funcAnnotations) {
            if (funcAnnotation is Method)
                funcName = funcAnnotation.name

            val annotations = funcAnnotation.annotationClass.annotations
            //尝试寻找方法注解的处理类
            val handler = annotations.find { it is Handler }
            if (handler != null && handler is Handler) {
                val handleClazz = applicationContext.getBean(handler.value.java)
                handleClazz.handle(point.`this`, func, point.args)
            }
        }

        return try {
            point.proceed()
        } catch (ex: Throwable) {
            val moduleName = this.resolveModuleName(point.`this`)
            var message = "${moduleName}-${funcName}异常"
            message = if (ex is NoticeUserException || ex is Error) {
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
            return this.httpMessageFactory.create(Status.Error, message)
        }
    }

    /**
     * 解析模块名
     */
    private fun resolveModuleName(obj: Any): String {
        val clazz = obj.javaClass
        // 1.默认使用类名作为模块名
        var moduleName = clazz.simpleName
        // 2.找到类的模块注解解析名称
        val module = clazz.annotations.find { it is Module }
        if (module != null && module is Module) {
            moduleName = module.name
        } else {
            // 3.尝试找类的name字段作为模块名称
            try {
                val nameGetter = clazz.getMethod("getName")
                moduleName = nameGetter.invoke(obj).toString()
            } catch (_: NoSuchMethodException) {
            }
        }

        return moduleName
    }
}
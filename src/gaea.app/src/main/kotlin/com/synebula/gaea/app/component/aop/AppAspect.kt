package com.synebula.gaea.app.component.aop

import com.fasterxml.jackson.databind.ObjectMapper
import com.synebula.gaea.app.IApplication
import com.synebula.gaea.app.struct.HttpMessage
import com.synebula.gaea.app.component.aop.annotation.MethodName
import com.synebula.gaea.app.component.aop.annotation.Handler
import com.synebula.gaea.app.component.aop.annotation.ModuleName
import com.synebula.gaea.data.message.Status
import com.synebula.gaea.log.ILogger
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Around
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.core.DefaultParameterNameDiscoverer

abstract class AppAspect {
    private var paramDiscover = DefaultParameterNameDiscoverer()

    private val mapper = ObjectMapper()

    @Autowired
    lateinit var logger: ILogger

    @Autowired
    lateinit var applicationContext: ApplicationContext

    /**
     * 定义切面的方法
     */
    abstract fun func()

    /**
     * 后置异常通知
     */
    @AfterThrowing("func()", throwing = "ex")
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
            if (funcAnnotation is MethodName)
                funcName = funcAnnotation.name
        }

        return try {
            point.proceed()
        } catch (ex: Throwable) {
            //找到类的模块名称，否则使用类名
            var moduleName = clazz.name
            if (IApplication::class.java.isAssignableFrom(clazz)) {
                moduleName = (point.`this` as IApplication).name
            } else {
                val name = clazz.annotations.find { it is ModuleName }
                if (name != null && name is ModuleName) {
                    moduleName = name.value
                }
            }
            val message = "$moduleName - $funcName 异常"
            logger.error(
                ex,
                "$message。Method args ${paramDiscover.getParameterNames(func)?.contentToString()} values is ${
                    mapper.writeValueAsString(
                        point.args
                    )
                }"
            )
            return HttpMessage(Status.Error, message)
        }
    }
}
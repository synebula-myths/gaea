package com.synebula.gaea.jpa.proxy.method

import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.jpa.proxy.method.resolver.AbstractMethodResolver
import com.synebula.gaea.jpa.proxy.method.resolver.DefaultMethodResolver
import com.synebula.gaea.jpa.proxy.method.resolver.FindMethodResolver
import com.synebula.gaea.jpa.proxy.method.resolver.PageMethodResolver
import com.synebula.gaea.db.query.Params
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * Jpa 方法映射包装类
 */
class JpaMethodProxy(
    /**
     * 方法需要实现的实体类
     */
    private var entityClazz: Class<*>
) {
    /**
     * 默认的方法映射配置(IRepository, IQuery 接口中定义的方法)
     */
    private val defaultMethodMapper: MutableMap<String, AbstractMethodResolver?> = LinkedHashMap()

    /**
     * 用户自定义的query注解方法处理
     */
    private val queryMethodMapper: MutableMap<String, AbstractMethodResolver> = LinkedHashMap()

    /**
     * 方法参数映射
     */
    var argumentResolver: AbstractMethodResolver? = null
        private set

    init {
        initDefaultMethodMapper()
    }

    /**
     * 匹配方法是否需要代理
     *
     * @param method 方法
     * @return ture/false
     */
    fun match(method: Method): Boolean {
        var isMatch = (defaultMethodMapper.containsKey(method.name)
                && defaultMethodMapper[method.name]!!.match(method, AbstractMethodResolver.MethodType.SourceMethod))

        // 如果默认代理方法没有匹配,则查找Query方法映射
        if (!isMatch) {
            isMatch = queryMethodMapper.containsKey(method.toString())
        }
        return isMatch
    }

    /**
     * 解析代理方法
     *
     * @param proxy  代理对象
     * @param method 源方法
     * @param args   参数列表
     * @return 执行结果
     */
    @Throws(NoSuchMethodException::class, InvocationTargetException::class, IllegalAccessException::class)
    fun proxyExecMethod(proxy: Any?, method: Method, args: Array<Any>): Any? {
        // 匹配方法是否需要代理
        if (defaultMethodMapper.containsKey(method.name)) {
            val resolver = defaultMethodMapper[method.name]
            // 匹配参数是否相同
            if (resolver!!.match(method, AbstractMethodResolver.MethodType.SourceMethod)) {
                //遍历代理对象, 找到合适的代理方法
                val targetMethod =
                    proxy!!.javaClass.getMethod(resolver.targetMethodName, *resolver.targetMethodParameters)
                return try {
                    // 开始执行代理方法
                    val mappingArguments = resolver.mappingArguments(args)
                    val result = targetMethod.invoke(proxy, *mappingArguments)
                    resolver.mappingResult(result)
                } catch (e: IllegalAccessException) {
                    throw RuntimeException(e)
                } catch (e: InvocationTargetException) {
                    throw RuntimeException(e)
                }
            }
        }
        // 如果默认代理方法没有匹配,则查找Query方法映射
        if (queryMethodMapper.containsKey(method.toString())) {
            val targetMethod = proxy!!.javaClass.getMethod(method.name, *method.parameterTypes)
            return targetMethod.invoke(proxy, *args)
        }
        throw RuntimeException(
            String.format(
                "方法[%s,%s]没有匹配的代理配置信息, 执行该方法前请先执行match方法判断",
                method.declaringClass.name, method.name
            )
        )
    }

    /**
     * 初始化默认的方法映射列表
     */
    private fun initDefaultMethodMapper() {
        defaultMethodMapper["add"] = DefaultMethodResolver("saveAndFlush")
            .sourceMethodParameters(IAggregateRoot::class.java).targetMethodParameters(Any::class.java)
        defaultMethodMapper["update"] = DefaultMethodResolver("saveAndFlush")
            .sourceMethodParameters(IAggregateRoot::class.java).targetMethodParameters(Any::class.java)
        defaultMethodMapper["remove"] = DefaultMethodResolver("deleteById")
            .sourceMethodParameters(Any::class.java).targetMethodParameters(Any::class.java)
        defaultMethodMapper["get"] = DefaultMethodResolver("findById")
            .sourceMethodParameters(Any::class.java).targetMethodParameters(Any::class.java)
        defaultMethodMapper["list"] = FindMethodResolver("findAll", entityClazz)
            .sourceMethodParameters(MutableMap::class.java).targetMethodParameters(Specification::class.java)
        defaultMethodMapper["count"] = FindMethodResolver("count", entityClazz)
            .sourceMethodParameters(MutableMap::class.java).targetMethodParameters(Specification::class.java)
        defaultMethodMapper["paging"] = PageMethodResolver("findAll", entityClazz)
            .sourceMethodParameters(Params::class.java)
            .targetMethodParameters(Specification::class.java, Pageable::class.java)
    }

    /**
     * 增加用户自定义Query注解方法映射信息
     *
     * @param method 需要添加的方法
     */
    fun addQueryMethodMapper(method: Method) {
        queryMethodMapper[method.toString()] = DefaultMethodResolver(method.name)
    }

    fun setArgumentResolver(argumentResolver: AbstractMethodResolver?): JpaMethodProxy {
        this.argumentResolver = argumentResolver
        return this
    }

    fun setEntityClazz(entityClazz: Class<*>) {
        this.entityClazz = entityClazz
    }
}
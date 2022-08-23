package com.synebula.gaea.spring.autoconfig

import org.springframework.cglib.proxy.MethodInterceptor
import org.springframework.cglib.proxy.MethodProxy
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

abstract class Proxy : MethodInterceptor, InvocationHandler {

    /**
     * JDK 方式代理代码
     */
    @Throws(Throwable::class)
    override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any? {
        return if (Any::class.java == method.declaringClass) {
            method.invoke(this, *args)
        } else {
            exec(proxy, method, args)
        }
    }

    /**
     * 暂时选用cglib 方式代理代码
     */
    @Throws(Throwable::class)
    override fun intercept(proxy: Any, method: Method, args: Array<Any>, methodProxy: MethodProxy): Any? {
        return if (Any::class.java == method.declaringClass) {
            methodProxy.invoke(this, args)
        } else {
            exec(proxy, method, args)
        }
    }

    /**
     * 执行代理方法
     *
     * @param proxy 代理对象
     * @param method 需要执行的方法
     * @param args   参数列表
     * @return 方法执行结果
     */
    abstract fun exec(proxy: Any, method: Method, args: Array<Any>): Any?
}
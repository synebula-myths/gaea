package com.synebula.gaea.spring.autoconfig

import org.springframework.beans.factory.FactoryBean
import org.springframework.cglib.proxy.Enhancer
import java.lang.reflect.Proxy as JdkProxy

/**
 * 代理生成工厂
 *
 * @param supertype 需要被代理的父类型
 * @param proxyType 代理类型:JDK 或 CGLIB
 */
abstract class Factory(
    protected val supertype: Class<*>,
    protected val proxyType: ProxyType = ProxyType.Cglib
) : FactoryBean<Any> {
    override fun getObject(): Any {
        val handler: Proxy = this.createProxy()

        //JDK 方式代理代码, 暂时选用cglib
        val proxy: Any = if (proxyType == ProxyType.JDK) {
            JdkProxy.newProxyInstance(
                this.supertype.classLoader, arrayOf(this.supertype), handler
            )
        } else { //cglib代理
            val enhancer = Enhancer()
            enhancer.setSuperclass(supertype)
            enhancer.setCallback(handler)
            enhancer.create()
        }

        return proxy
    }

    override fun getObjectType(): Class<*> {
        return supertype
    }

    override fun isSingleton(): Boolean {
        return true
    }

    abstract fun createProxy(): Proxy
}
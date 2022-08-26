package com.synebula.gaea.jpa.proxy

import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.FactoryBean
import org.springframework.cglib.proxy.Enhancer
import org.springframework.data.repository.Repository

class JpaRepositoryFactory(
    private val beanFactory: BeanFactory,
    private val interfaceType: Class<*>,
    private val implBeanNames: List<String>
) : FactoryBean<Any> {
    override fun getObject(): Any {
        val handler: JpaRepositoryProxy<*, *, *> = JpaRepositoryProxy<Repository<Any, Any>, Any, Any>(
            beanFactory,
            interfaceType, implBeanNames
        )

        //JDK 方式代理代码, 暂时选用cglib
        //Object proxy = Proxy.newProxyInstance(this.interfaceType.getClassLoader(), new Class[]{this.interfaceType}, handler);

        //cglib代理
        val enhancer = Enhancer()
        enhancer.setSuperclass(interfaceType)
        enhancer.setCallback(handler)
        return enhancer.create()
    }

    override fun getObjectType(): Class<*> {
        return interfaceType
    }

    override fun isSingleton(): Boolean {
        return true
    }
}
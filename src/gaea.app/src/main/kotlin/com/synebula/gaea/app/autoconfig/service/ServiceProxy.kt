package com.synebula.gaea.app.autoconfig.service

import com.synebula.gaea.data.serialization.IObjectMapper
import com.synebula.gaea.domain.repository.IRepository
import com.synebula.gaea.domain.service.IService
import com.synebula.gaea.domain.service.Service
import com.synebula.gaea.domain.service.ServiceDependency
import com.synebula.gaea.spring.autoconfig.Proxy
import org.springframework.beans.factory.BeanFactory
import java.io.InvalidClassException
import java.lang.reflect.Method

class ServiceProxy(
    private var supertype: Class<*>, private var beanFactory: BeanFactory, implementBeanNames: Array<String> = arrayOf()
) : Proxy() {

    private var service: IService<*>

    init {
        // 如果没有实现类, 使用Service类代理
        if (implementBeanNames.isEmpty()) {
            // 如果没有实现类并且没有ServiceDependency注解, 则抛出异常
            if (!this.supertype.declaredAnnotations.any { it.annotationClass == ServiceDependency::class }) {
                throw InvalidClassException(
                    "interface ${this.supertype.name} must has implementation class or annotation by ${ServiceDependency::class.qualifiedName}"
                )
            }
            val serviceDependency = this.supertype.getDeclaredAnnotation(ServiceDependency::class.java)
            val repo = this.beanFactory.getBean(serviceDependency.repo.java)
            val mapper = this.beanFactory.getBean(IObjectMapper::class.java)
            val constructor = Service::class.java.getConstructor(
                Class::class.java, IRepository::class.java, IObjectMapper::class.java
            )
            this.service = constructor.newInstance(serviceDependency.clazz.java, repo, mapper)
        } else {
            this.service = this.beanFactory.getBean(implementBeanNames[0]) as IService<*>
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
    override fun exec(proxy: Any, method: Method, args: Array<Any>): Any? {
        try {
            val proxyMethod = this.service.javaClass.getDeclaredMethod(method.name, *method.parameterTypes)
            return proxyMethod.invoke(this.service, *args)
        } catch (ex: NoSuchMethodException) {
            throw NoSuchMethodException("method [${method.toGenericString()}] not implements in class [${this.service::class.java}], you must implements interface [${this.supertype.name}] ")
        }
    }
}
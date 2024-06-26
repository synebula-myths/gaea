package com.synebula.gaea.app.autoconfig.service

import com.synebula.gaea.bus.IBus
import com.synebula.gaea.data.serialization.IObjectMapper
import com.synebula.gaea.domain.repository.IRepository
import com.synebula.gaea.domain.repository.IRepositoryFactory
import com.synebula.gaea.domain.service.Domain
import com.synebula.gaea.domain.service.IService
import com.synebula.gaea.domain.service.Service
import com.synebula.gaea.exception.NoticeUserException
import com.synebula.gaea.spring.autoconfig.Proxy
import org.springframework.beans.factory.BeanFactory
import org.springframework.core.ResolvableType
import java.io.InvalidClassException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class ServiceProxy(
    private var supertype: Class<*>,
    private var beanFactory: BeanFactory
) : Proxy() {

    private var service: IService<*>

    init {
        // 如果没有实现类, 使用Service类代理
        // 如果没有实现类并且没有ServiceDependency注解, 则抛出异常
        if (!this.supertype.declaredAnnotations.any { it.annotationClass == Domain::class }) {
            throw InvalidClassException(
                "interface ${this.supertype.name} must has implementation class or annotation by ${Domain::class.qualifiedName}"
            )
        }
        val domain = this.supertype.getDeclaredAnnotation(Domain::class.java)

        // repository工厂对象
        val defaultRepositoryFactory = this.beanFactory.getBean(IRepositoryFactory::class.java)
        val mapper = this.beanFactory.getBean(IObjectMapper::class.java)

        val constructor = Service::class.java.getConstructor(
            Class::class.java, IRepository::class.java, IObjectMapper::class.java
        )
        this.service =
            constructor.newInstance(
                domain.clazz.java,
                defaultRepositoryFactory.createRawRepository(domain.clazz.java),
                mapper
            )

        // 尝试注入IBus对象
        val bus = Service::class.java.getDeclaredField("bus")
        val iBusObjectProvider = this.beanFactory.getBeanProvider<IBus<*>>(ResolvableType.forField(bus))
        iBusObjectProvider.ifAvailable { busBean ->
            bus.isAccessible = true
            bus.set(this.service, busBean)
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
            val proxyMethod = this.service.javaClass.getMethod(method.name, *method.parameterTypes)
            return proxyMethod.invoke(this.service, *args)
        } catch (ex: NoSuchMethodException) {
            throw NoSuchMethodException("method [${method.toGenericString()}] not implements in class [${this.service::class.java}], you must implements interface [${this.supertype.name}] ")
        } catch (ex: InvocationTargetException) {
            if (ex.cause is Error || ex.cause is NoticeUserException) {
                throw ex.targetException!!
            }
            throw ex
        }
    }
}
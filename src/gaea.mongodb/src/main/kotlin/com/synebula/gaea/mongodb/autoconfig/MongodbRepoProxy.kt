package com.synebula.gaea.mongodb.autoconfig

import com.synebula.gaea.domain.repository.IRepository
import com.synebula.gaea.log.ILogger
import com.synebula.gaea.mongodb.query.MongodbQuery
import com.synebula.gaea.mongodb.repository.MongodbRepository
import com.synebula.gaea.query.IQuery
import com.synebula.gaea.spring.autoconfig.Proxy
import org.springframework.beans.factory.BeanFactory
import org.springframework.data.mongodb.core.MongoTemplate
import java.io.InvalidClassException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType

class MongodbRepoProxy(
    private var supertype: Class<*>, private var beanFactory: BeanFactory, implementBeanNames: Array<String> = arrayOf()
) : Proxy() {

    private var repo: IRepository<*, *>? = null

    private var query: IQuery<*, *>? = null

    init {
        if (this.supertype.interfaces.any { it == IRepository::class.java }) {
            // 如果是IRepository子接口
            if (implementBeanNames.isEmpty()) {
                val genericInterfaces = this.supertype.genericInterfaces.find {
                    it.typeName.startsWith(IRepository::class.java.typeName)
                }!!
                val constructor = MongodbRepository::class.java.getConstructor(
                    Class::class.java, MongoTemplate::class.java
                )
                this.repo = constructor.newInstance(
                    (genericInterfaces as ParameterizedType).actualTypeArguments[0],
                    this.beanFactory.getBean(MongoTemplate::class.java)
                )
            } else {
                this.repo = this.beanFactory.getBean(implementBeanNames[0]) as IRepository<*, *>
            }
        } else {
            // 否则是IQuery子接口
            if (implementBeanNames.isEmpty()) {
                val genericInterfaces = this.supertype.genericInterfaces.find {
                    it.typeName.startsWith(IQuery::class.java.typeName)
                }!!
                val constructor = MongodbQuery::class.java.getConstructor(
                    Class::class.java, MongoTemplate::class.java, ILogger::class.java
                )
                this.query = constructor.newInstance(
                    (genericInterfaces as ParameterizedType).actualTypeArguments[0],
                    this.beanFactory.getBean(MongoTemplate::class.java),
                    this.beanFactory.getBean(ILogger::class.java),
                )
            } else {
                this.query = this.beanFactory.getBean(implementBeanNames[0]) as IQuery<*, *>
            }
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
        val proxyClazz = if (this.repo != null) {
            this.repo!!.javaClass
        } else if (this.query != null) {
            this.query!!.javaClass
        } else
            throw InvalidClassException("class ${this.supertype.name} property repo and query are both null")

        try {
            val proxyMethod: Method = proxyClazz.getDeclaredMethod(method.name, *method.parameterTypes)
            return proxyMethod.invoke(this.repo, *args)
        } catch (ex: NoSuchMethodException) {
            throw NoSuchMethodException("method [${method.toGenericString()}] not implements in class [${proxyClazz}], you must implements interface [${this.supertype.name}] ")
        }
    }

}
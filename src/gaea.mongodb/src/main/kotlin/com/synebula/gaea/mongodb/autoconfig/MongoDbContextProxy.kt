package com.synebula.gaea.mongodb.autoconfig

import com.synebula.gaea.db.context.IDbContext
import com.synebula.gaea.domain.repository.IRepository
import com.synebula.gaea.mongodb.db.query.MongodbQuery
import com.synebula.gaea.mongodb.repository.MongodbRepository
import com.synebula.gaea.db.query.IQuery
import com.synebula.gaea.reflect.getGenericInterface
import com.synebula.gaea.spring.autoconfig.Proxy
import org.springframework.beans.factory.BeanFactory
import org.springframework.data.mongodb.core.MongoTemplate
import java.lang.reflect.Method

class MongoDbContextProxy(
    private var supertype: Class<*>, private var beanFactory: BeanFactory
) : Proxy() {

    /**
     * 实际的执行类
     */
    private var context: Any?

    init {
        if (this.supertype.interfaces.any { it == IDbContext::class.java }) {
            this.context = beanFactory.getBean(IDbContext::class.java)
            if (context == null) {
                val constructor = IDbContext::class.java.getConstructor(Class::class.java, MongoTemplate::class.java)
                this.context = constructor.newInstance(this.beanFactory.getBean(MongoTemplate::class.java))

            }
        } else {
            // 判断接口类型
            val clazz: Class<*> // 代理服务类型
            val interfaceClazz: Class<*> // 代理服务接口

            if (this.supertype.interfaces.any { it == IQuery::class.java }) {
                clazz = MongodbQuery::class.java
                interfaceClazz = IQuery::class.java
            } else {
                clazz = MongodbRepository::class.java
                interfaceClazz = IRepository::class.java
            }
            val constructor = clazz.getConstructor(Class::class.java, MongoTemplate::class.java)
            this.context = constructor.newInstance(
                this.supertype.getGenericInterface(interfaceClazz)!!.actualTypeArguments[0],
                this.beanFactory.getBean(MongoTemplate::class.java)
            )
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
            val proxyMethod: Method = this.context!!.javaClass.getMethod(method.name, *method.parameterTypes)
            return proxyMethod.invoke(this.context, *args)
        } catch (ex: NoSuchMethodException) {
            throw NoSuchMethodException("method [${method.toGenericString()}] not implements in class [${this.context!!.javaClass}], you must implements interface [${this.supertype.name}] ")
        }
    }

}
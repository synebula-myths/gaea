package com.synebula.gaea.mongodb.autoconfig

import com.synebula.gaea.spring.autoconfig.Factory
import com.synebula.gaea.spring.autoconfig.Proxy
import org.springframework.beans.factory.BeanFactory

class MongoDbContextFactory(
    supertype: Class<*>,
    var beanFactory: BeanFactory,
) : Factory(supertype) {
    override fun createProxy(): Proxy {
        return MongoDbContextProxy(supertype, this.beanFactory)
    }
}
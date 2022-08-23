package com.synebula.gaea.mongodb.repository

import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.domain.repository.IRepository
import com.synebula.gaea.domain.repository.IRepositoryFactory
import org.springframework.data.mongodb.core.MongoTemplate

class MongodbRepositoryFactory(var template: MongoTemplate) : IRepositoryFactory {

    /**
     * 创建IRepository接口类型
     */
    override fun createRawRepository(clazz: Class<*>): IRepository<*, *> {
        val constructor = MongodbRepository::class.java.getConstructor(Class::class.java, MongoTemplate::class.java)
        return constructor.newInstance(clazz, this.template)
    }

    /**
     * 创建IRepository接口类型
     */
    override fun <T : IAggregateRoot<I>, I> createRepository(clazz: Class<T>): IRepository<T, I> {
        return MongodbRepository(clazz, template)
    }
}
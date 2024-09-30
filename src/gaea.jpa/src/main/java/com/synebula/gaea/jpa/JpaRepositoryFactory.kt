package com.synebula.gaea.jpa

import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.domain.repository.IRepository
import com.synebula.gaea.domain.repository.IRepositoryFactory
import jakarta.persistence.EntityManager

class JpaRepositoryFactory(private var entityManager: EntityManager) : IRepositoryFactory {
    override fun createRawRepository(clazz: Class<*>): IRepository<*, *> {
        val constructor = JpaRepository::class.java.getConstructor(Class::class.java, EntityManager::class.java)
        return constructor.newInstance(clazz, this.entityManager)
    }

    override fun <T : IAggregateRoot<I>, I> createRepository(clazz: Class<T>): IRepository<T, I> {
        return JpaRepository(clazz, this.entityManager)
    }
}
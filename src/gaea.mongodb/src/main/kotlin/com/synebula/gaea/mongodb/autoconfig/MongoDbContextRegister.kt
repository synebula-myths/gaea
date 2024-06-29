package com.synebula.gaea.mongodb.autoconfig

import com.synebula.gaea.db.context.IDbContext
import com.synebula.gaea.db.query.IQuery
import com.synebula.gaea.domain.repository.IRepository
import com.synebula.gaea.spring.autoconfig.Register
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.core.annotation.AnnotationAttributes
import org.springframework.core.type.AnnotationMetadata

class MongoDbContextRegister : Register() {
    override fun scan(metadata: AnnotationMetadata): Map<String, BeanDefinition> {
        val result = mutableMapOf<String, BeanDefinition>()

        // 获取注解参数信息:basePackages
        val attributes = AnnotationAttributes(
            metadata.getAnnotationAttributes(
                MongoDbRepositoryScan::class.java.name
            ) ?: mapOf()
        )
        val basePackages = attributes.getStringArray("basePackages")
        val beanDefinitions = this.doScan(
            basePackages,
            arrayOf(this.interfaceFilter(arrayOf(IDbContext::class.java, IQuery::class.java, IRepository::class.java)))
        )
        beanDefinitions.forEach { beanDefinition ->
            // 获取实际的bean类型
            val beanClazz: Class<*> = try {
                Class.forName(beanDefinition.beanClassName)
            } catch (ex: ClassNotFoundException) {
                throw ex
            }

            // 尝试获取实际继承类型
            val implBeanDefinitions = this.doScan(basePackages, arrayOf(this.interfaceFilter(arrayOf(beanClazz))))

            if (implBeanDefinitions.isNotEmpty()) {
                implBeanDefinitions.forEach {
                    result[it.beanClassName!!] = it
                }
            } else { // 构造BeanDefinition
                val builder = BeanDefinitionBuilder.genericBeanDefinition(beanClazz)
                builder.addConstructorArgValue(beanClazz)
                builder.addConstructorArgValue(this._beanFactory)
                val definition = builder.rawBeanDefinition as GenericBeanDefinition
                definition.beanClass = MongoDbContextFactory::class.java
                definition.autowireMode = GenericBeanDefinition.AUTOWIRE_BY_TYPE
                result[beanClazz.name] = definition
            }
        }
        return result
    }

    private fun addDefaultProxyBean(result: MutableMap<String, BeanDefinition>) {
        // IRepository proxy
        val builder = BeanDefinitionBuilder.genericBeanDefinition(IRepository::class.java)
        builder.addConstructorArgValue(IRepository::class.java)
        builder.addConstructorArgValue(this._beanFactory)
        builder.addConstructorArgValue(emptyArray<String>())
        val definition = builder.rawBeanDefinition as GenericBeanDefinition
        definition.beanClass = MongoDbContextFactory::class.java
        definition.autowireMode = GenericBeanDefinition.AUTOWIRE_BY_TYPE
        result[IRepository::class.java.name] = definition
    }
}
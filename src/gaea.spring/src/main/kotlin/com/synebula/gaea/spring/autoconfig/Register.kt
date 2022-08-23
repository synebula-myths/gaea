package com.synebula.gaea.spring.autoconfig

import org.springframework.beans.BeansException
import org.springframework.beans.factory.BeanClassLoaderAware
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.context.EnvironmentAware
import org.springframework.context.ResourceLoaderAware
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.env.Environment
import org.springframework.core.io.ResourceLoader
import org.springframework.core.type.AnnotationMetadata
import org.springframework.core.type.classreading.MetadataReader
import org.springframework.core.type.classreading.MetadataReaderFactory
import org.springframework.core.type.filter.TypeFilter
import org.springframework.util.ClassUtils
import java.util.*

abstract class Register : ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanClassLoaderAware,
    EnvironmentAware, BeanFactoryAware {

    protected var _classLoader: ClassLoader? = null
    protected var _environment: Environment? = null
    protected var _resourceLoader: ResourceLoader? = null
    protected var _beanFactory: BeanFactory? = null

    override fun registerBeanDefinitions(metadata: AnnotationMetadata, registry: BeanDefinitionRegistry) {
        val beanDefinitions = this.scan(metadata)
        beanDefinitions.forEach { registry.registerBeanDefinition(it.key, it.value) }
    }

    abstract fun scan(metadata: AnnotationMetadata): Map<String, BeanDefinition>

    /**
     * 根据过滤器扫描直接包下bean
     *
     * @param packages 指定的扫描包
     * @param filters  过滤器
     * @return 扫描后的bean定义
     */
    protected fun doScan(packages: Array<String>?, filters: Array<TypeFilter>): List<BeanDefinition> {
        val scanner: ClassPathScanningCandidateComponentProvider =
            object : ClassPathScanningCandidateComponentProvider() {
                override fun isCandidateComponent(beanDefinition: AnnotatedBeanDefinition): Boolean {
                    try {
                        val metadata = beanDefinition.metadata
                        val target = ClassUtils.forName(metadata.className, _classLoader)
                        return !target.isAnnotation
                    } catch (ignored: Exception) {
                    }
                    return false
                }
            }
        scanner.environment = _environment!!
        scanner.resourceLoader = _resourceLoader!!
        for (filter in filters) {
            scanner.addIncludeFilter(filter)
        }
        val beanDefinitions: MutableList<BeanDefinition> = LinkedList()
        for (basePackage in packages!!) {
            beanDefinitions.addAll(scanner.findCandidateComponents(basePackage))
        }
        return beanDefinitions
    }

    /**
     * 获取指定接口的类型过滤器
     *
     * @param interfaces 需要过滤的父接口类型
     * @param onlyInterface 是否只获取接口类型
     * @return 类型过滤器
     */
    protected fun interfaceFilter(interfaces: Array<Class<*>>, onlyInterface: Boolean = false): TypeFilter {
        return TypeFilter { metadataReader: MetadataReader, _: MetadataReaderFactory? ->
            // 如果只获取接口类型且当前类型非接口 直接返回
            if (onlyInterface && !metadataReader.annotationMetadata.isInterface)
                return@TypeFilter false

            var matched = false
            val interfaceNames = metadataReader.classMetadata.interfaceNames
            // 如果当前类型继承接口有任一需要过滤的接口则说明复合条件
            for (interfaceName in interfaceNames) {
                matched = interfaces.any { clazz -> clazz.name == interfaceName }
            }
            matched
        }
    }

    override fun setResourceLoader(resourceLoader: ResourceLoader) {
        this._resourceLoader = resourceLoader
    }

    override fun setBeanClassLoader(classLoader: ClassLoader) {
        this._classLoader = classLoader
    }

    override fun setEnvironment(environment: Environment) {
        this._environment = environment
    }

    @Throws(BeansException::class)
    override fun setBeanFactory(beanFactory: BeanFactory) {
        this._beanFactory = beanFactory
    }
}
package com.synebula.gaea.jpa.proxy

import org.springframework.beans.BeansException
import org.springframework.beans.factory.BeanClassLoaderAware
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.context.EnvironmentAware
import org.springframework.context.ResourceLoaderAware
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.annotation.AnnotationAttributes
import org.springframework.core.env.Environment
import org.springframework.core.io.ResourceLoader
import org.springframework.core.type.AnnotationMetadata
import org.springframework.core.type.classreading.MetadataReader
import org.springframework.core.type.classreading.MetadataReaderFactory
import org.springframework.core.type.filter.TypeFilter
import org.springframework.util.ClassUtils
import java.util.*
import java.util.stream.Collectors

class JpaRepositoryRegister : ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanClassLoaderAware,
    EnvironmentAware,
    BeanFactoryAware {
    private lateinit var environment: Environment
    private lateinit var resourceLoader: ResourceLoader
    private var classLoader: ClassLoader? = null
    private var beanFactory: BeanFactory? = null
    override fun registerBeanDefinitions(metadata: AnnotationMetadata, registry: BeanDefinitionRegistry) {
        val attributes = AnnotationAttributes(
            metadata.getAnnotationAttributes(
                JpaRepositoryProxyScan::class.java.name
            ) ?: mapOf()
        )
        val basePackages = attributes.getStringArray("basePackages")
        val scanInterfaces = attributes.getClassArray("scanInterfaces")
        // 过滤scanInterfaces接口内容
        val filter = getSubObjectTypeFilter(scanInterfaces)
        val beanDefinitions = scan(basePackages, arrayOf(filter))

        // 遍历处理接口
        for (beanDefinition in beanDefinitions) {
            // 获取RepositoryFor注解信息
            val beanClazz: Class<*> = try {
                Class.forName(beanDefinition.beanClassName)
            } catch (e: ClassNotFoundException) {
                throw RuntimeException(e)
            }
            val beanClazzTypeFilter = getSubObjectTypeFilter(arrayOf(beanClazz))
            val implClazzDefinitions = scan(basePackages, arrayOf(beanClazzTypeFilter))
            for (definition in implClazzDefinitions) {
                definition.isAutowireCandidate = false
                registry.registerBeanDefinition(Objects.requireNonNull(definition.beanClassName), definition)
            }
            // 构建bean定义
            // 1 bean参数
            val implBeanNames = implClazzDefinitions.stream().map { obj: BeanDefinition -> obj.beanClassName }
                .collect(Collectors.toList())
            val builder = BeanDefinitionBuilder.genericBeanDefinition(beanClazz)
            builder.addConstructorArgValue(beanFactory)
            builder.addConstructorArgValue(beanClazz)
            builder.addConstructorArgValue(implBeanNames)
            val definition = builder.rawBeanDefinition as GenericBeanDefinition
            definition.beanClass = JpaRepositoryFactory::class.java
            definition.autowireMode = GenericBeanDefinition.AUTOWIRE_BY_TYPE
            registry.registerBeanDefinition(beanClazz.name, definition)
        }
    }

    /**
     * 根据过滤器扫描直接包下bean
     *
     * @param packages 指定的扫描包
     * @param filters  过滤器
     * @return 扫描后的bean定义
     */
    private fun scan(packages: Array<String>?, filters: Array<TypeFilter>): List<BeanDefinition> {
        val scanner: ClassPathScanningCandidateComponentProvider =
            object : ClassPathScanningCandidateComponentProvider() {
                override fun isCandidateComponent(beanDefinition: AnnotatedBeanDefinition): Boolean {
                    try {
                        val metadata = beanDefinition.metadata
                        val target = ClassUtils.forName(metadata.className, classLoader)
                        return !target.isAnnotation
                    } catch (ignored: Exception) {
                    }
                    return false
                }
            }
        scanner.environment = environment
        scanner.resourceLoader = resourceLoader
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
     * 获取父接口实现对象的类型过滤器
     *
     * @param interfaces 父接口
     * @return 类型过滤器
     */
    private fun getSubObjectTypeFilter(interfaces: Array<Class<*>>?): TypeFilter {
        return TypeFilter { metadataReader: MetadataReader, _: MetadataReaderFactory? ->
            val interfaceNames = metadataReader.classMetadata.interfaceNames
            var matched = false
            for (interfaceName in interfaceNames) {
                matched = Arrays.stream(interfaces)
                    .anyMatch { clazz: Class<*> -> clazz.name == interfaceName }
            }
            matched
        }
    }

    override fun setResourceLoader(resourceLoader: ResourceLoader) {
        this.resourceLoader = resourceLoader
    }

    override fun setBeanClassLoader(classLoader: ClassLoader) {
        this.classLoader = classLoader
    }

    override fun setEnvironment(environment: Environment) {
        this.environment = environment
    }

    @Throws(BeansException::class)
    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory
    }
}
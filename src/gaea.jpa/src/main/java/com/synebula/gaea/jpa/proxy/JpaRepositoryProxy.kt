package com.synebula.gaea.jpa.proxy

import com.synebula.gaea.jpa.proxy.method.JpaMethodProxy
import jakarta.persistence.EntityManager
import javassist.*
import javassist.bytecode.AnnotationsAttribute
import javassist.bytecode.MethodInfo
import javassist.bytecode.SignatureAttribute
import javassist.bytecode.annotation.Annotation
import javassist.bytecode.annotation.BooleanMemberValue
import javassist.bytecode.annotation.StringMemberValue
import org.springframework.beans.BeansException
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.cglib.proxy.MethodInterceptor
import org.springframework.cglib.proxy.MethodProxy
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation
import org.springframework.data.mapping.context.MappingContext
import org.springframework.data.querydsl.EntityPathResolver
import org.springframework.data.querydsl.SimpleEntityPathResolver
import org.springframework.data.repository.Repository
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class JpaRepositoryProxy<T : Repository<S, ID>?, S, ID>(
    beanFactory: BeanFactory,
    interfaceType: Class<*>,
    implementBeanNames: List<String>?
) : MethodInterceptor {    //InvocationHandler {

    //JPA 默认Entity Manager上下文, 如不用该上下文则没有事务管理器
    private val EntityManagerName = "org.springframework.orm.jpa.SharedEntityManagerCreator#0"

    /**
     * JPA代理对象
     */
    private var jpaRepository: JpaRepositoryImplementation<*, *>? = null

    /**
     * bean注册器
     */
    protected var beanFactory: BeanFactory

    /**
     * 方法映射管理器
     */
    protected var jpaMethodProxy: JpaMethodProxy

    /**
     * 需要代理的接口类型
     */
    protected var interfaceType: Class<*>

    /**
     * 接口实现bean名称
     */
    protected var implementBeanNames: List<String>

    init {
        try {
            this.beanFactory = beanFactory
            this.implementBeanNames = implementBeanNames ?: listOf()
            this.interfaceType = interfaceType

            // 设置方法映射查询参数类(Entity类型)
            val type = interfaceType.genericInterfaces[0]
            val typeArguments = (type as ParameterizedType).actualTypeArguments
            jpaMethodProxy = JpaMethodProxy(Class.forName(typeArguments[0].typeName))


            // 创建虚假的JpaRepository接口
            val repoClazz = createJpaRepoClazz(*typeArguments)
            val jpaRepositoryFactoryBean: JpaRepositoryFactoryBean<*, *, *> = createJPARepositoryFactoryBean(repoClazz)
            jpaRepository = jpaRepositoryFactoryBean.getObject() as JpaRepositoryImplementation<*, *>
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e)
        }
    }

    /**
     * JDK 方式代理代码, 暂时选用cglib
     */
    @Deprecated("")
    @Throws(Throwable::class)
    operator fun invoke(proxy: Any?, method: Method, args: Array<Any>): Any? {
        return if (Any::class.java == method.declaringClass) {
            method.invoke(proxy, *args)
        } else {
            execMethod(method, args)
        }
    }

    /**
     * 暂时选用cglib 方式代理代码
     */
    @Throws(Throwable::class)
    override fun intercept(o: Any, method: Method, args: Array<Any>, methodProxy: MethodProxy): Any {
        return if (Any::class.java == method.declaringClass) {
            methodProxy.invoke(this, args)
        } else {
            execMethod(method, args)!!
        }
    }

    /**
     * 执行代理方法
     *
     * @param method 需要执行的方法
     * @param args   参数列表
     * @return 方法执行结果
     * @throws Throwable 异常
     */
    @Throws(Throwable::class)
    private fun execMethod(method: Method, args: Array<Any>): Any? {
        // 找到对应代理方法, 代理执行
        return if (jpaMethodProxy.match(method)) {
            try {
                jpaMethodProxy.proxyExecMethod(jpaRepository, method, args)
            } catch (ex: Exception) {
                throw RuntimeException(
                    String.format(
                        "对象[%s]代理执行方法[%s.%s]出错",
                        jpaMethodProxy.javaClass, interfaceType.name, method.name
                    ), ex
                )
            }
        } else {
            // 找不到代理方法则查找具体实现类执行
            if (implementBeanNames.isEmpty()) throw RuntimeException(
                String.format(
                    "找不到[%s.%s]对应的代理方法",
                    method.declaringClass.name, method.name
                )
            ) else {
                val bean = beanFactory.getBean(implementBeanNames[0])
                val proxyMethod = bean.javaClass.getMethod(method.name, *method.parameterTypes)
                proxyMethod.invoke(bean, *args)
            }
        }
    }

    /**
     * 使用javassist创建虚拟的jpa repo类
     *
     * @param typeArgs 泛型参数
     * @return 虚拟的jpa repo类形
     */
    @Suppress("unchecked_cast")
    private fun createJpaRepoClazz(vararg typeArgs: Type): Class<T> {
        return try {
            val pool = ClassPool.getDefault()
            val jpaRepoCt = pool[JpaRepositoryImplementation::class.java.name]
            val clazzName = String.format("%sRepository", typeArgs[0].typeName)
            val repoCt = pool.makeInterface(clazzName, jpaRepoCt)
            val typeArguments = arrayOfNulls<SignatureAttribute.TypeArgument>(typeArgs.size)
            for (i in typeArgs.indices) {
                typeArguments[i] = SignatureAttribute.TypeArgument(SignatureAttribute.ClassType(typeArgs[i].typeName))
            }
            val ac = SignatureAttribute.ClassSignature(
                null,
                null,
                arrayOf(SignatureAttribute.ClassType(jpaRepoCt.name, typeArguments))
            )
            repoCt.genericSignature = ac.encode()
            addClassQueryMethod(repoCt)
            repoCt.toClass() as Class<T>
        } catch (ex: Exception) {
            throw RuntimeException(ex)
        }
    }

    /**
     * 给虚拟接口添加Query注解方法
     *
     * @param ctClass jpa虚拟接口
     */
    @Throws(NotFoundException::class, CannotCompileException::class, ClassNotFoundException::class)
    private fun addClassQueryMethod(ctClass: CtClass) {
        // 找到Query注解方法并加入到虚拟接口中
        val interfaceCtClass = ClassPool.getDefault()[interfaceType.name]
        for (ctMethod in interfaceCtClass.methods) {
            val query = ctMethod.getAnnotation(Query::class.java)
            if (query != null) {
                val method = CtNewMethod.abstractMethod(
                    ctMethod.returnType, ctMethod.name,
                    ctMethod.parameterTypes, arrayOfNulls(0), ctClass
                )
                val methodInfo = method.methodInfo
                var modifying: Modifying? = null
                //查找有无@Modifing注解，有的化虚拟接口也需要加上
                val annotation = ctMethod.getAnnotation(Modifying::class.java)
                if (annotation != null) {
                    modifying = annotation as Modifying
                }

                // 增加Query注解
                val attribute = buildQueryAttribute(methodInfo, query as Query, modifying)
                methodInfo.addAttribute(attribute)
                ctClass.addMethod(method)
            }
        }

        // Query注解方法加入到代理中
        for (method in interfaceType.methods) {
            val annotation: Any? = method.getAnnotation(Query::class.java)
            if (annotation != null) {
                jpaMethodProxy.addQueryMethodMapper(method)
            }
        }
    }

    /**
     * 创建javassist方法Query注解
     *
     * @param methodInfo 方法信息
     * @param query      query注解实例
     * @param modifying  Modifying注解
     * @return 注解信息
     */
    private fun buildQueryAttribute(methodInfo: MethodInfo, query: Query, modifying: Modifying?): AnnotationsAttribute {
        val cp = methodInfo.constPool
        val attribute = AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag)
        val queryAnnotation = Annotation(Query::class.java.name, cp)
        queryAnnotation.addMemberValue("value", StringMemberValue(query.value, cp))
        queryAnnotation.addMemberValue("countQuery", StringMemberValue(query.countQuery, cp))
        queryAnnotation.addMemberValue("countProjection", StringMemberValue(query.countProjection, cp))
        queryAnnotation.addMemberValue("nativeQuery", BooleanMemberValue(query.nativeQuery, cp))
        queryAnnotation.addMemberValue("name", StringMemberValue(query.name, cp))
        queryAnnotation.addMemberValue("countName", StringMemberValue(query.countName, cp))
        if (modifying != null) {
            val modifyingAnnotation = Annotation(
                Modifying::class.java.name, cp
            )
            modifyingAnnotation.addMemberValue(
                "flushAutomatically",
                BooleanMemberValue(modifying.flushAutomatically, cp)
            )
            modifyingAnnotation.addMemberValue(
                "clearAutomatically",
                BooleanMemberValue(modifying.clearAutomatically, cp)
            )
            attribute.annotations = arrayOf(queryAnnotation, modifyingAnnotation)
        } else attribute.setAnnotation(queryAnnotation)
        return attribute
    }

    /**
     * 创建JpaRepositoryFactoryBean对象
     *
     * @param jpaRepositoryClass 需要创建的 JPA JpaRepository Class
     * @return JpaRepositoryFactoryBean对象
     */
    private fun createJPARepositoryFactoryBean(jpaRepositoryClass: Class<out T>): JpaRepositoryFactoryBean<T, S, ID> {
        // jpa 默认使用改名成EntityManager, 若用默认则没有事务上下文
        val entityManager = beanFactory.getBean(EntityManagerName) as EntityManager
        val repositoryFactoryBean = JpaRepositoryFactoryBean(jpaRepositoryClass)
        repositoryFactoryBean.setEntityManager(entityManager)
        repositoryFactoryBean.setBeanFactory(beanFactory)
        repositoryFactoryBean.setBeanClassLoader(JpaRepositoryFactoryBean::class.java.classLoader)
        repositoryFactoryBean.setMappingContext(beanFactory.getBean("jpaMappingContext") as MappingContext<*, *>)
        repositoryFactoryBean.setEntityPathResolver(object : ObjectProvider<EntityPathResolver> {
            @Throws(BeansException::class)
            override fun getObject(vararg objects: Any): EntityPathResolver {
                return SimpleEntityPathResolver("")
            }

            @Throws(BeansException::class)
            override fun getIfAvailable(): EntityPathResolver? {
                return null
            }

            @Throws(BeansException::class)
            override fun getIfUnique(): EntityPathResolver? {
                return null
            }

            @Throws(BeansException::class)
            override fun getObject(): EntityPathResolver {
                return SimpleEntityPathResolver("")
            }
        })
        repositoryFactoryBean.afterPropertiesSet()
        return repositoryFactoryBean
    }
}
package com.synebula.gaea.app.bus

import com.synebula.gaea.bus.DomainSubscribe
import com.synebula.gaea.bus.IBus
import com.synebula.gaea.bus.Subscribe
import com.synebula.gaea.data.message.messageTopic
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import java.lang.reflect.Method


@Component
class EventBusSubscriberProcessor : BeanPostProcessor {

    // 事件总线bean由Spring IoC容器负责创建，这里只需要通过@Autowired注解注入该bean即可使用事件总线
    @Autowired
    var bus: IBus<Any>? = null

    @Throws(BeansException::class)
    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        return bean
    }

    //对于每个容器执行了初始化的 bean，如果这个 bean 的某个方法注解了@Subscribe,则将该 bean 注册到事件总线
    @Throws(BeansException::class)
    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        // for each method in the bean
        val methods: Array<Method> = bean.javaClass.methods
        for (method in methods) {
            if (method.isAnnotationPresent(Subscribe::class.java)) {
                val subscribe = method.getAnnotation(Subscribe::class.java)
                if (subscribe.topics.isEmpty())
                    bus?.register(bean, method)
                else
                    bus?.register(subscribe.topics, bean, method)
            }
            if (method.isAnnotationPresent(DomainSubscribe::class.java)) {
                val domainSubscribe = method.getAnnotation(DomainSubscribe::class.java)
                var topic = messageTopic(domainSubscribe.domain, domainSubscribe.messageClass.java)
                if (domainSubscribe.domainClass != Nothing::class)
                    topic = messageTopic(domainSubscribe.domainClass.java, domainSubscribe.messageClass.java)
                bus?.register(arrayOf(topic), bean, method)
            }
        }
        return bean
    }
}
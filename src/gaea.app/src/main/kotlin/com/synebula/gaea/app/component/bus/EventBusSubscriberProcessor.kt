package com.synebula.gaea.app.component.bus

import com.google.common.eventbus.Subscribe
import com.synebula.gaea.bus.IMessageBus
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import java.lang.reflect.Method


@Component
class EventBusSubscriberProcessor : BeanPostProcessor {

    // 事件总线bean由Spring IoC容器负责创建，这里只需要通过@Autowired注解注入该bean即可使用事件总线
    @Autowired
    var messageBus: IMessageBus? = null

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
            // check the annotations on that method
            val annotations: Array<Annotation> = method.annotations
            for (annotation in annotations) {
                // if it contains Subscribe annotation
                if (annotation.annotationClass == Subscribe::class) {
                    // 如果这是一个Guava @Subscribe注解的事件监听器方法，说明所在bean实例
                    // 对应一个Guava事件监听器类，将该bean实例注册到Guava事件总线
                    messageBus?.register(bean)
                    return bean
                }
            }
        }
        return bean
    }

}
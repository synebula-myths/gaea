package com.synebula.gaea.spring.aop.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.synebula.gaea.log.ILogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.lang.reflect.Method

@Component
class AccessLogHandler : AnnotationHandler {
    private val mapper = ObjectMapper()

    @Autowired
    lateinit var logger: ILogger

    override fun handle(clazz: Class<Any>, func: Method, args: Array<Any>, exception: Exception?) {
        val attributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes
        val request = attributes.request
        logger.info(
            "${request.method} ${request.requestURL} from ${request.remoteAddr}, call function ${clazz.name}.${func.name}, args: ${
                mapper.writeValueAsString(
                    args
                )
            }"
        )
    }
}
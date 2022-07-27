package com.synebula.gaea.bus

import java.util.logging.Level
import java.util.logging.Logger


/** Simple logging handler for subscriber exceptions.  */
internal class LoggingHandler<T : Any> : SubscriberExceptionHandler<T> {
    override fun handleException(exception: Throwable?, context: SubscriberExceptionContext<T>) {
        val logger = logger(context)
        if (logger.isLoggable(Level.SEVERE)) {
            logger.log(Level.SEVERE, message(context), exception)
        }
    }

    companion object {
        private fun <T : Any> logger(context: SubscriberExceptionContext<T>): Logger {
            return Logger.getLogger(Bus::class.java.name + "." + context.bus.identifier)
        }

        private fun <T : Any> message(context: SubscriberExceptionContext<T>): String {
            val method = context.subscriberMethod
            return ("Exception thrown by subscriber method "
                    + method.name
                    + '('
                    + method.parameterTypes[0].name
                    + ')'
                    + " on subscriber "
                    + context.subscriber
                    + " when dispatching message: "
                    + context.message)
        }
    }
}
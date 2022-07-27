/*
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.synebula.gaea.bus.messagebus

import com.synebula.gaea.bus.Dispatcher
import com.synebula.gaea.bus.LoggingHandler
import com.synebula.gaea.bus.SubscriberExceptionHandler
import java.util.concurrent.Executor

/**
 * An [MessageBus] that takes the Executor of your choice and uses it to dispatch events,
 * allowing dispatch to occur asynchronously.
 *
 * @author Cliff
 * @since 10.0
 */
class AsyncMessageBus<T : Any> : MessageBus<T> {
    /**
     * Creates a new AsyncEventBus that will use `executor` to dispatch events. Assigns `identifier` as the bus's name for logging purposes.
     *
     * @param identifier short name for the bus, for logging purposes.
     * @param executor Executor to use to dispatch events. It is the caller's responsibility to shut
     * down the executor after the last event has been posted to this event bus.
     */
    constructor(identifier: String, executor: Executor) : super(
        identifier,
        executor,
        Dispatcher.legacyAsync(),
        LoggingHandler()
    )

    /**
     * Creates a new AsyncEventBus that will use `executor` to dispatch events.
     *
     * @param executor Executor to use to dispatch events. It is the caller's responsibility to shut
     * down the executor after the last event has been posted to this event bus.
     * @param subscriberExceptionHandler Handler used to handle exceptions thrown from subscribers.
     * See [SubscriberExceptionHandler] for more information.
     * @since 16.0
     */
    constructor(executor: Executor, subscriberExceptionHandler: SubscriberExceptionHandler<T>) : super(
        "default",
        executor,
        Dispatcher.legacyAsync(),
        subscriberExceptionHandler
    )

    /**
     * Creates a new AsyncEventBus that will use `executor` to dispatch events.
     *
     * @param executor Executor to use to dispatch events. It is the caller's responsibility to shut
     * down the executor after the last event has been posted to this event bus.
     */
    constructor(executor: Executor) : super(
        "default",
        executor,
        Dispatcher.legacyAsync(),
        LoggingHandler()
    )
}
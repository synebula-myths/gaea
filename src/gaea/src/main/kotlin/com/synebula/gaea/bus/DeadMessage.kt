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
package com.synebula.gaea.bus

/**
 * Wraps an message that was posted, but which had no subscribers and thus could not be delivered.
 *
 *
 * Registering a DeadMessage subscriber is useful for debugging or logging, as it can detect
 * misconfigurations in a system's message distribution.
 *
 * @author Cliff
 * @since 10.0
 *
 * Creates a new DeadMessage.
 *
 * @param source object broadcasting the DeadMessage (generally the [Bus]).
 * @param message the message that could not be delivered.
 */
class DeadMessage(val source: Any?, val message: Any?) {

    override fun toString(): String {
        return "DeadMessage(source=$source, message=$message)"
    }
}
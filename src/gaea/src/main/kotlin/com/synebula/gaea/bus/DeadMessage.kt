
package com.synebula.gaea.bus

/**
 * Wraps a message that was posted, but which had no subscribers and thus could not be delivered.
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
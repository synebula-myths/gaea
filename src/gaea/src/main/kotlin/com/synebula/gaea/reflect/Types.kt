package com.synebula.gaea.reflect

import com.synebula.gaea.bus.SubscriberRegistry

object Types {
    /**
     * 获取类的所有父类型。
     */
    fun supertypes(clazz: Class<*>): Set<Class<*>> {
        val supertypes = mutableSetOf<Class<*>>()
        supertypes.add(clazz)
        if (clazz.superclass != null)
            supertypes.addAll(SubscriberRegistry.flattenHierarchy(clazz.superclass))
        if (clazz.interfaces.isNotEmpty()) {
            supertypes.addAll(clazz.interfaces.map { SubscriberRegistry.flattenHierarchy(it) }.reduce { r, c ->
                val all = r.toMutableSet()
                all.addAll(c)
                all
            })
        }
        return supertypes
    }
}
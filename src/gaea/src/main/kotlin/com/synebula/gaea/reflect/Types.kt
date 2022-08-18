package com.synebula.gaea.reflect

object Types {
    /**
     * 获取类的所有父类型。
     */
    fun supertypes(clazz: Class<*>): Set<Class<*>> {
        val supertypes = mutableSetOf<Class<*>>()
        supertypes.add(clazz)
        if (clazz.interfaces.isNotEmpty()) {
            supertypes.addAll(clazz.interfaces.map { supertypes(it) }.reduce { r, c ->
                val all = r.toMutableSet()
                all.addAll(c)
                all
            })
        }
        if (clazz.superclass != null)
            supertypes.addAll(supertypes(clazz.superclass))
        return supertypes
    }
}
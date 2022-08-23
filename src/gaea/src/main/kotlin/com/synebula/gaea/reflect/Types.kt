package com.synebula.gaea.reflect

import java.lang.reflect.ParameterizedType

/**
 * 获取类的所有父类型。
 */
fun Class<*>.supertypes(): Set<Class<*>> {
    val supertypes = mutableSetOf<Class<*>>()
    supertypes.add(this)
    if (this.interfaces.isNotEmpty()) {
        supertypes.addAll(this.interfaces.map { it.supertypes() }.reduce { r, c ->
            val all = r.toMutableSet()
            all.addAll(c)
            all
        })
    }
    if (this.superclass != null)
        supertypes.addAll(this.superclass.supertypes())
    return supertypes
}

fun Class<*>.getGenericInterface(interfaceClazz: Class<*>): ParameterizedType? {
    val type = this.genericInterfaces.find { it.typeName.startsWith(interfaceClazz.typeName) }
    return if (type == null) null
    else type as ParameterizedType

}

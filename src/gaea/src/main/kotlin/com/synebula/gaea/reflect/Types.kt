package com.synebula.gaea.reflect

import java.lang.reflect.Field
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


/**
 * 查找类字段, 可以查找包括继承类的私有字段
 *
 * @param name 字段名称
 * @return 字段类型
 */
fun Class<*>.findField(name: String): Field? {
    var field: Field? = null
    for (f in this.declaredFields) {
        if (f.name == name) {
            field = f
        }
    }
    if (field == null) {
        val superclass = this.superclass
        if (superclass != Any::class.java) {
            field = superclass.findField(name)
        }
    }
    return field
}

/**
 * 获取对象字段信息字符串列表。
 */
fun Class<*>.fieldNames(): List<String> {
    val names = mutableListOf<String>()
    for (f in this.declaredFields) {
        names.add(f.name)
    }
    val superclass = this.superclass
    if (superclass != Any::class.java) {
        names.addAll(superclass.fieldNames())
    }
    return names
}



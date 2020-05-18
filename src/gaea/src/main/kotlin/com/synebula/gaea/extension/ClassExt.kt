package com.synebula.gaea.extension

import java.lang.reflect.Field
import java.util.*

/**
 * 系统类型
 */
val SystemClasses = arrayOf(
        "String",
        "Date",
        "Int",
        "Double",
        "Float",
        "BigDecimal",
        "Decimal")

/**
 * 深度获取所有字段信息字符串列表。
 * @param prefix 前缀字符串
 */
fun Class<*>.fields(prefix: String = ""): List<String> {
    val names = mutableListOf<String>()
    this.declaredFields.forEach { field ->
        val fullName = if (prefix.isNotEmpty()) "$prefix.${field.name}" else field.name
        names.add(fullName)
        if (!field.type.isPrimitive && !field.type.isArray && !SystemClasses.contains(field.type.simpleName)) {
            names.addAll(field.type.fields(fullName))
        }
    }
    return names
}

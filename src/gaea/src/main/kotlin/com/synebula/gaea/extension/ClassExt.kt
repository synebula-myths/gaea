package com.synebula.gaea.extension

/**
 * 获取对象字段信息字符串列表。
 */
fun Class<*>.fieldNames(): List<String> {
    val names = mutableListOf<String>()
    this.declaredFields.forEach { field ->
        names.add(field.name)
    }
    return names
}

package com.synebula.gaea.io.scan

/**
 * 类过滤器，用于过滤不需要加载的类<br></br>
 * @author alex
 * @version 0.0.1
 * @since 2016年9月18日 下午4:41:29
 */
interface IClassFilter {
    fun accept(clazz: Class<*>): Boolean
}

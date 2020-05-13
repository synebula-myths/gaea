package com.synebula.gaea.data

/**
 * 对象转换器，支持对象之间的转换。
 *
 * @author alex
 * @version 0.1
 * @since 2018 18-2-2
 */
interface IObjectConverter {

    /**
     * 转换源对象到目标对象。
     *
     * @param src  源对象。
     * @param dest 目标对象。
     * @param <T>  目标对象类型。
     * @return 目标对象
     */
    fun <T> convert(src: Any, dest: Class<T>): T
}

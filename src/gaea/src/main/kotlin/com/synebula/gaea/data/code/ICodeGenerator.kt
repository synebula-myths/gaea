package com.synebula.gaea.data.code

/**
 * 继承本接口的类，都能实现编号生成工作。
 *
 * @author alex
 * @version 0.0.1
 * @since 2016年10月24日 上午10:41:03
 */
interface ICodeGenerator<T> {
    /**
     * 生成编号。
     *
     * @return
     */
    fun generate(): T
}

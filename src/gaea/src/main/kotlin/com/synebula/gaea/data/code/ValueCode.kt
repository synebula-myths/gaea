package com.synebula.gaea.data.code

/**
 * 默认值编号，返回默认值。
 *
 * @author alex
 * @version 0.0.1
 * @since 2016年10月24日 下午4:07:10
 */
class ValueCode(var value: String) : ICodeGenerator<String> {

    /*
	 * Method
	 */
    override fun generate(): String {
        return this.value
    }
}

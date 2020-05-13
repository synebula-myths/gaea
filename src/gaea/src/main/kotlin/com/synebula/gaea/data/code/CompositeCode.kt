package com.synebula.gaea.data.code

/**
 * 组合编号，根据模板组合生成编号。
 *
 * @author alex
 * @version 0.0.1
 * @since 2016年10月24日 下午2:53:50
 */
class CompositeCode(val generators: List<ICodeGenerator<*>>?) : ICodeGenerator<String> {

    /*
	 * Method
	 */

    override fun generate(): String {
        if (this.generators == null || generators.size == 0)
            return ""
        val buffer = StringBuffer()
        for (generator in generators) {
            buffer.append(generator.generate())
        }
        return buffer.toString()
    }
}

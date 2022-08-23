package com.synebula.gaea.data.code

import java.util.*
import kotlin.math.pow

/**
 * 固定长度随机编号生成。
 * @author alex
 * @version 0.0.1
 * @since 2016年10月24日 上午10:58:05
 */
class FixedRandomCode(
    //生成的随机编号长度。
    var length: Int,
) : ICodeGenerator<String> {

    /**
     * 随机数生成器。
     */
    private val random = Random()

    /**
     * 计算最大长度,不能超过9
     */
    internal var calcMaxLength = 10

    override fun generate(): String {
        /*
		 * 如果最大长度超过特定长度（默认10），则分多次随机数计算，然后拼接。
		 * 最后一次的数量是对计算长度对特定长度的取模。如13前次要循环计算1次，最后计算3位随机数。
		 */
        val buffer = StringBuffer()
        var format = String.format("%s%d%dd", "%", 0, calcMaxLength)
        val count = this.length / calcMaxLength
        for (i in 0 until count) {
            buffer.append(String.format(format, (random.nextDouble() * 10.0.pow(calcMaxLength.toDouble())).toInt()))
        }
        val last = this.length % calcMaxLength
        if (last != 0) {
            format = String.format("%s%d%dd", "%", 0, last)
            buffer.append(String.format(format, (random.nextDouble() * 10.0.pow(last.toDouble())).toInt()))
        }
        return buffer.toString()
    }

}

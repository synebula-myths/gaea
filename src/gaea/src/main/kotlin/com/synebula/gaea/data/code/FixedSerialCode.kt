package com.synebula.gaea.data.code

/**
 * 序列编号，自增的编号。
 *
 * @author alex
 * @version 0.0.1
 * @since 2016年10月24日 下午3:17:36
 */
/**
 * 构造
 *
 * @param length 流水号固定长度，不足前面默认补零，值小于零则表示为不定长。
 * @param seed   种子，初始值。
 * @param step   步进，每次增长数量。
 */
class FixedSerialCode(
        /**
         * code长度，不足前面补零。
         */
        var length: Int = 0,
        /**
         * 种子，初始值。
         */
        var seed: Int = 1,
        /**
         * 步进，每次增长数量。
         */
        var step: Int = 1
) : ICodeGenerator<String> {

    @Synchronized
    override fun generate(): String {
        val value = this.seed
        this.seed += this.step
        var format = "%d"
        if (this.length > 0) {
            format = String.format("%s%d%dd", "%", 0, this.length)
        }
        return String.format(format, value)
    }
}
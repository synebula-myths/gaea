package com.synebula.gaea.data.code

import java.text.SimpleDateFormat
import java.util.Date

/**
 * @author reize
 * @version 0.0.1
 * @since 2016年10月24日 下午3:42:47
 * @param pattern 时间格式化模式。不需要定制可以选择默认构造方法："yyyyMMdd"。
 * 参数：年=y，月=M，日=d，时=H，分=m，秒=s，毫秒=S。位数最好使用默认最大长度。
 */
class DateCode(pattern: String = "yyyyMMdd") : ICodeGenerator<Long> {
    var formator = SimpleDateFormat()

    init {
        formator.applyPattern(pattern)
    }

    override fun generate(): Long {
        return java.lang.Long.parseLong(formator.format(Date()))
    }
}

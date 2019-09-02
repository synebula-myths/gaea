package com.synebula.gaea.data.code

import java.util.UUID

/**
 * 全球唯一编号生成。
 *
 * @author reize
 * @version 0.0.1
 * @since 2016年10月24日 下午2:46:09
 */
class GUIDCode : ICodeGenerator<String> {

    override fun generate(): String {
        return UUID.randomUUID().toString().replace("-".toRegex(), "")
    }
}

package com.synebula.gaea.test

import com.synebula.gaea.data.code.SnowflakeCode
import junit.framework.TestCase

class CodeGenerateTest : TestCase() {
    fun testSnowflake() {
        val snowflakeCode = SnowflakeCode(1, 1)
        for (i in 0..10) {
            Thread.sleep(1000)
            println(snowflakeCode.generate())
        }
    }
}
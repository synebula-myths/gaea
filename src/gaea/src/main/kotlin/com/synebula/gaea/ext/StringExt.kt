package com.synebula.gaea.ext

import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * 首字母小写
 */
fun String.firstCharLowerCase(): String {
    return if (Character.isLowerCase(this.elementAt(0)))
        this
    else
        StringBuilder().append(Character.toLowerCase(this.elementAt(0)))
                .append(this.substring(1)).toString()
}

/**
 * 获取字符串的MD值
 */
fun String.toMd5(): String {
    val secretBytes = try {
        MessageDigest.getInstance("md5").digest(this.toByteArray())
    } catch (e: NoSuchAlgorithmException) {
        throw RuntimeException("没有这个md5算法！")
    }
    var md5code = BigInteger(1, secretBytes).toString(16)
    for (i in 0 until 32 - md5code.length) {
        md5code = "0$md5code"
    }
    return md5code
}

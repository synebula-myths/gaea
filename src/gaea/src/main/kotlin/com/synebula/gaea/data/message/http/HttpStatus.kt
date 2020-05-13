package com.synebula.gaea.data.message.http

/**
 * 状态类型。
 *
 * @author alex
 * @version 0.0.1
 * @since 2016年9月6日 下午3:27:55
 */
enum class HttpStatus(val code: Int) {
    /**
     * 成功
     */
    Success(200),

    /**
     * 失败
     */
    Failure(400),

    /**
     * 错误
     */
    Error(500);

    companion object {
        fun valueOf(code: Int): HttpStatus {
            return when (code) {
                200 -> HttpStatus.Success
                400 -> HttpStatus.Failure
                else -> HttpStatus.Error
            }
        }
    }

}

package com.synebula.gaea.data.type

/**
 * 状态类型。
 *
 * @author reize
 * @version 0.0.1
 * @since 2016年9月6日 下午3:27:55
 */
enum class Status(val code: Int) {
    /**
     * 成功
     */
    success(200),

    /**
     * 失败
     */
    failure(400),

    /**
     * 错误
     */
    error(500);

    companion object {
        fun valueOf(code: Int): Status {
            return when (code) {
                200 -> Status.success
                400 -> Status.failure
                else -> Status.error
            }
        }
    }

}

package com.synebula.gaea.data.message

object Status {
    /**
     * 成功
     */
    const val SUCCESS = 200

    /**
     * 重新授权
     */
    const val REAUTHORIZE = 205

    /**
     * 失败
     */
    const val FAILURE = 400

    /**
     * 未授权
     */
    const val UNAUTHORIZED = 401

    /**
     * 权限不足
     */
    const val FORBIDDEN = 403

    /**
     * 错误
     */
    const val ERROR = 500
}
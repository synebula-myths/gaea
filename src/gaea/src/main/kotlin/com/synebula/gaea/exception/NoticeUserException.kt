package com.synebula.gaea.exception

/**
 * 需要通知给用户的异常
 */
class NoticeUserException(message: String, cause: Exception? = null) : Exception(message, cause)
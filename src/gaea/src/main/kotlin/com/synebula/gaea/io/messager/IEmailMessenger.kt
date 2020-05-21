package com.synebula.gaea.io.messager

interface IEmailMessenger {
    fun sendMessage(receivers: List<String>, subject: String, content: String)
}
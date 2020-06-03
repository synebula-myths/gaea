package com.synebula.gaea.io.comm

interface IEmailMessenger {
    /**
     * 发送消息
     *
     * @param subject 邮件标题
     * @param content 邮件内容
     * @param receivers 邮件接受者
     * @param files 附件
     */
    fun sendMessage(subject: String, content: String,
                    receivers: List<String>, files: Map<String, String> = mapOf())
}
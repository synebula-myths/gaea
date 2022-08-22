package com.synebula.gaea.app.component

import com.synebula.gaea.io.comm.IEmailMessenger
import com.synebula.gaea.log.ILogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import java.io.File

@Component
class EmailMessenger(var mailSender: JavaMailSender?) : IEmailMessenger {

    @Value("\${spring.mail.host:}")
    var host = ""

    @Value("\${spring.mail.port:}")
    var port = ""

    @Value("\${spring.mail.sender:}")
    var sender = ""

    @Value("\${spring.mail.username:}")
    var username = ""

    @Value("\${spring.mail.password:}")
    var password = ""

    @Autowired
    lateinit var logger: ILogger

    /**
     * 发送消息
     *
     * @param subject 邮件标题
     * @param content 邮件内容,支持html
     * @param receivers 邮件接受者
     * @param files 附件
     */
    override fun sendMessage(
        subject: String,
        content: String,
        receivers: List<String>,
        files: Map<String, String>
    ): Map<String, Boolean> {
        val result = mutableMapOf<String, Boolean>()
        this.check()
        receivers.forEach { receiver ->
            result[receiver] = true
            if (this.mailSender == null)
                throw Exception("没有配置JavaMailSender的实现实例，请重新配置")
            try {
                val mail = this.mailSender!!.createMimeMessage()
                val mimeMessageHelper = MimeMessageHelper(mail, true, "utf-8")
                mimeMessageHelper.setFrom(sender)
                mimeMessageHelper.setTo(receiver)
                mimeMessageHelper.setSubject(subject)
                mimeMessageHelper.setText(content, true)

                files.forEach { (name, path) ->
                    val file = FileSystemResource(File(path))
                    mimeMessageHelper.addAttachment(name, file)
                }
                this.mailSender!!.send(mail) //发送
            } catch (e: Exception) {
                logger.error(e, "发送邮件[$subject]至地址[$receiver]失败")
                result[receiver] = false
            }
        }
        return result
    }

    private fun check() {
        if (this.host.isEmpty())
            throw RuntimeException("邮件Host信息没有配置, 请配置项: mail.host")
        if (this.username.isEmpty())
            throw RuntimeException("邮件用户名信息没有配置, 请配置项: mail.username")
        if (this.password.isEmpty())
            throw RuntimeException("邮件密码信息没有配置, 请配置项: mail.password")
        if (this.sender.isEmpty())
            throw RuntimeException("邮件发送人信息没有配置, 请配置项: mail.sender")
    }
}
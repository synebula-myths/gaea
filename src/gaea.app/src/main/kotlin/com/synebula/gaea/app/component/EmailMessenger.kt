package com.synebula.gaea.app.component

import com.synebula.gaea.io.comm.IEmailMessenger
import com.synebula.gaea.log.ILogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import java.io.File

@Component
class EmailMessenger : IEmailMessenger {
    private var mailSender = JavaMailSenderImpl()

    @Value("\${mail.host}")
    var host = ""

    @Value("\${mail.port}")
    var port = ""

    @Value("\${mail.sender}")
    var sender = ""

    @Value("\${mail.username}")
    var username = ""

    @Value("\${mail.password}")
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
    override fun sendMessage(subject: String, content: String, receivers: List<String>, files: Map<String, String>) {
        this.check()
        mailSender.host = host
        mailSender.username = username
        mailSender.password = password
        mailSender.port = if (port.isEmpty()) port.toInt() else 25
        receivers.forEach {
            try {
                val mail = mailSender.createMimeMessage()
                val mimeMessageHelper = MimeMessageHelper(mail, true, "utf-8")
                mimeMessageHelper.setFrom(sender)
                mimeMessageHelper.setTo(it)
                mimeMessageHelper.setSubject(subject)
                mimeMessageHelper.setText(content, true)

                files.forEach { (name, path) ->
                    val file = FileSystemResource(File(path))
                    mimeMessageHelper.addAttachment(name, file)
                }
                mailSender.send(mail) //发送
            } catch (e: Exception) {
                logger.error(e, "发送邮件[$subject]至地址[$it]失败")
            }
        }
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
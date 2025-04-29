package com.example.greenenergyapp

import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class MailSender(
    private val user: String,
    private val pass: String
) {
    fun sendMail(toEmail: String, subject: String, message: String, callback: (Boolean, String?) -> Unit) {
        Thread {
            try {
                val props = Properties().apply {
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.host", "smtp.gmail.com")
                    put("mail.smtp.port", "587")
                }

                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(user, pass)
                    }
                })

                val mimeMessage = MimeMessage(session).apply {
                    setFrom(InternetAddress(user))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
                    setSubject(subject)
                    setText(message)
                }

                Transport.send(mimeMessage)
                callback(true, null)
            } catch (e: Exception) {
                e.printStackTrace()
                callback(false, e.message)
            }
        }.start()
    }
}

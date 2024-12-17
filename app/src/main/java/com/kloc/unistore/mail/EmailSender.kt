package com.kloc.unistore.mail

import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


object EmailSender {
    @Throws(MessagingException::class)
    fun sendEmail(
        toEmail: String?,
        subject: String?,
        body: String?,
        fromEmail: String?,
        password: String?
    ) {
        // SMTP Server configuration
        val props = Properties()
        props["mail.smtp.host"] = "smtp.gmail.com" // SMTP Host
        props["mail.smtp.port"] = "587" // TLS Port
        props["mail.smtp.auth"] = "true" // Enable Authentication
        props["mail.smtp.starttls.enable"] = "true" // Enable StartTLS

        // Create Session
        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(fromEmail, password) // Email and Password
            }
        })
        try {
            // Create Email Message
            val message: Message = MimeMessage(session)
            message.setFrom(InternetAddress(fromEmail))
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
            message.subject = subject
            message.setText(body)

            // Send Email
            Transport.send(message)
        } catch (e: MessagingException) {
            throw MessagingException("Error while sending email: " + e.message)
        }
    }
}


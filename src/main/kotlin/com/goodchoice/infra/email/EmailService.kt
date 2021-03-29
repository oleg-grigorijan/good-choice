package com.goodchoice.infra.email

import kotlinx.css.*
import kotlinx.css.properties.TextDecoration
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.mail.javamail.MimeMessagePreparator
import org.springframework.stereotype.Component
import java.util.UUID.randomUUID
import javax.annotation.PostConstruct

@Component
class EmailServiceImpl(private val emailSender: JavaMailSender) {

    @PostConstruct
    fun f() {
        val primaryColor = Color("#007bff")
        val confirmationToken = "${randomUUID()}-${randomUUID()}"
        val confirmationUrl = "https://good-choice-api.herokuapp.com/emailConfirmation?token=$confirmationToken"
        val html = createHTML().html {
            head {
                title("GoodChoice email confirmation")
            }
            body {
                div {
                    style = css {
                        width = 100.pct
                        maxWidth = 600.px
                        marginLeft = LinearDimension.auto
                        marginRight = LinearDimension.auto
                    }

                    h1 { +"Welcome to GoodChoice" }
                    h2 { +"Finish your account registration" }
                    p {
                        +"Thanks for your interest in GoodChoice."
                        +" Please, confirm your email address and get full access to many cool features we made for you."
                        +" Just press the button below."
                    }
                    a(href = confirmationUrl, target = "_blank") {
                        style = css {
                            backgroundColor = primaryColor
                            color = Color.white
                            textDecoration = TextDecoration.none
                            padding = "10px 20px"
                            borderRadius = 5.px
                            display = Display.inlineBlock
                            fontSize = 1.2.em
                            fontWeight = FontWeight.bold
                        }
                        +"Confirm email"
                    }
                    br()
                    p { +"If that doesn't work, copy and paste the following link in your browser." }
                    a(href = confirmationUrl, target = "_blank") {
                        style = css {
                            color = primaryColor
                            textDecoration = TextDecoration.none
                        }
                        +confirmationUrl
                    }
                    p { +"Make your good choice!" }
                }
            }
        }
        sendSimpleMessage("oleg.grigorijan@gmail.com", "Email confirmation", html)
    }

    fun sendSimpleMessage(to: String, subject: String, text: String) {
        MimeMessagePreparator { message ->
            MimeMessageHelper(message).apply {
                setFrom("goodchoice.bot.local@gmail.com", "GoodChoice Bot")
                setTo(to)
                setSubject(subject)
                setText(text, true)
            }
        }.let { emailSender.send(it) }
    }
}

fun css(builder: CSSBuilder.() -> Unit): String {
    return CSSBuilder().apply(builder).toString()
}

package com.goodchoice.infra.config

import com.goodchoice.domain.auth.model.SpringAuth
import com.goodchoice.domain.auth.persistence.AuthRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

@Configuration
@EnableWebSecurity
class SecurityConfig(private val authRepo: AuthRepository) : WebSecurityConfigurerAdapter() {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(UserDetailsService { email ->
            authRepo.getByEmailOrNull(email)
                ?.let { SpringAuth(it.id, it.email, it.role, it.password) }
                ?: throw UsernameNotFoundException(email)
        })
    }

    override fun configure(http: HttpSecurity) {
        http {
            authorizeRequests {
                // Swagger
                authorize(GET, "/", permitAll)
                authorize(GET, "/swagger-ui/**", permitAll)
                authorize(GET, "/docs", permitAll)
                authorize(GET, "/docs.*", permitAll)
                authorize(GET, "/docs/swagger-config", permitAll)

                authorize(POST, "/reviewers", permitAll)
                authorize("/**", authenticated)
            }
            httpBasic {
                authenticationEntryPoint = AuthenticationEntryPoint { _, response, _ ->
                    response.status = UNAUTHORIZED.value()
                }
            }
            formLogin {
                loginProcessingUrl = "/login"
                authenticationSuccessHandler = AuthenticationSuccessHandler { _, response, _ ->
                    response.status = NO_CONTENT.value()
                }
                authenticationFailureHandler = AuthenticationFailureHandler { _, response, _ ->
                    response.status = UNAUTHORIZED.value()
                }
            }
            sessionManagement {
                sessionCreationPolicy = STATELESS
            }
            csrf {
                disable()
            }
        }
    }
}

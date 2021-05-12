package com.goodchoice.infra.config

import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.common.model.ExternalServicesProperties
import com.goodchoice.infra.security.AuthUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebSecurity
class SecurityConfig(private val authService: AuthService) : WebSecurityConfigurerAdapter() {

    @Bean
    fun corsConfigurer(externalServicesProps: ExternalServicesProperties) = object : WebMvcConfigurer {
        override fun addCorsMappings(registry: CorsRegistry) {
            registry.addMapping("/**").allowedOrigins(externalServicesProps.webFrontend)
        }
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(AuthUserDetailsService(authService))
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

                authorize(POST, "/reviewers/self", permitAll)

                authorize(POST, "/employees/self", permitAll)
                authorize(GET, "/employees/self/invitations", permitAll)

                authorize(GET, "/brands", permitAll)
                authorize(GET, "/brands/*", permitAll)

                authorize(GET, "/subjects", permitAll)
                authorize(GET, "/subjects/*", permitAll)

                authorize(GET, "/subjects/tags", permitAll)
                authorize(GET, "/subjects/tags/*", permitAll)

                authorize(GET, "/subjects/*/reviews", permitAll)

                authorize("/**", authenticated)
            }
            httpBasic {
                authenticationEntryPoint = AuthenticationEntryPoint { _, response, _ ->
                    response.status = UNAUTHORIZED.value()
                }
            }
//            formLogin {
//                loginProcessingUrl = "/login"
//                authenticationSuccessHandler = AuthenticationSuccessHandler { _, response, _ ->
//                    response.status = NO_CONTENT.value()
//                }
//                authenticationFailureHandler = AuthenticationFailureHandler { _, response, _ ->
//                    response.status = UNAUTHORIZED.value()
//                }
//            }
            sessionManagement {
                sessionCreationPolicy = STATELESS
            }
            csrf {
                disable()
            }
            cors {}
        }

        http.formLogin().disable()
    }
}

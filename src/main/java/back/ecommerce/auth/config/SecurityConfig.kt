package back.ecommerce.auth.config

import back.ecommerce.auth.filter.JwtAuthenticationFilter
import back.ecommerce.auth.service.JwtAuthenticationProvider
import back.ecommerce.auth.service.TokenExtractor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val tokenExtractor: TokenExtractor,
    private val jwtAuthenticationProvider: JwtAuthenticationProvider
) {

    @Bean
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter {
        return JwtAuthenticationFilter(tokenExtractor, jwtAuthenticationProvider)
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.formLogin {
            it.disable()
        }.httpBasic {
            it.disable()
        }.csrf {
            it.disable()
        }.logout {
            it.disable()
        }.authorizeHttpRequests {
//            it.requestMatchers(
//                "/api/login",
//                "/api/signup",
//                "/api/auth/exist-nickname",
//                "/api/chat-rooms/exit",
//                "/api/chat-rooms/participant"
//            ).permitAll()
            it.requestMatchers("/api/**").permitAll()
        }.sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
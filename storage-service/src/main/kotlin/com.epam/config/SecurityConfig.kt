package com.epam.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import java.util.stream.Collectors


@EnableWebSecurity
@ConditionalOnProperty(prefix = "test", name = ["security"], havingValue = "true", matchIfMissing = true)
class SecurityConfig {

    @Bean
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity
            .csrf().disable()
            .authorizeHttpRequests()
            .antMatchers("/actuator/**")
            .permitAll()

        httpSecurity
            .csrf().disable()
            .authorizeHttpRequests()
            .antMatchers(HttpMethod.GET, "/api/storages")
            .hasAnyRole("ADMIN", "USER")

        httpSecurity
            .csrf().disable()
            .authorizeHttpRequests()
            .antMatchers(HttpMethod.POST, "/api/storages")
            .hasRole("ADMIN")

        httpSecurity
            .csrf().disable()
            .authorizeHttpRequests()
            .antMatchers(HttpMethod.DELETE, "/api/storages")
            .hasRole("ADMIN")

        httpSecurity.oauth2ResourceServer { it ->
            it.jwt {
                it.jwtAuthenticationConverter(jwtAuthenticationConverter())
            }
        }

        return httpSecurity.build()
    }

    private fun jwtAuthenticationConverter(): Converter<Jwt, out AbstractAuthenticationToken> {
        val jwtConverter = JwtAuthenticationConverter()
        jwtConverter.setJwtGrantedAuthoritiesConverter(RealmRoleConverter())
        return jwtConverter
    }

    private class RealmRoleConverter: Converter<Jwt, Collection<GrantedAuthority>> {
        override fun convert(source: Jwt): Collection<GrantedAuthority>? {
            val realmAccess = source.claims["realm_access"] as Map<String, List<String>>
            return realmAccess["roles"]
                ?.map { roleName -> "ROLE_$roleName" }
                ?.map { role: String? -> SimpleGrantedAuthority(role) }
        }
    }
}

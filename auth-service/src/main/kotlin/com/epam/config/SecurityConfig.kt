package com.epam.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.annotation.Order
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*

//@Configuration
//@Import(OAuth2AuthorizationServerConfiguration::class)
class SecurityConfig {

    @Bean
    fun qwe(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity.authorizeHttpRequests() {
            it.anyRequest().permitAll()
        }

        return httpSecurity.build()
    }

//    @Bean
//    @Order(1)
//    fun authServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain? {
//        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
//        return http.formLogin(Customizer.withDefaults()).build();
//    }
//
//    @Bean
//    @Order(2)
//    @Throws(java.lang.Exception::class)
//    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain? {
//        http
//            .authorizeHttpRequests { authorize ->
//                authorize
//                    .anyRequest().authenticated()
//            } // Form login handles the redirect to the login page from the
//            // authorization server filter chain
//            .formLogin(Customizer.withDefaults())
//        return http.build()
//    }
//
//    @Bean
//    fun users(): UserDetailsService {
//        val admin = User.withDefaultPasswordEncoder()
//            .username("admin")
//            .password("admin")
//            .roles("ADMIN")
//            .build()
//
//        val user = User.withDefaultPasswordEncoder()
//            .username("user")
//            .password("user")
//            .roles("user")
//            .build()
//
//        return InMemoryUserDetailsManager(admin, user)
//    }

//    @Bean
//    fun registeredClientRepository(): RegisteredClientRepository {
//        val client = RegisteredClient
//            .withId(UUID.randomUUID().toString())
//            .clientId("postman")
//            .clientSecret("postman")
//            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
//            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
//            .redirectUri("http://localhost:8080/authorized")
//            .build()
//
//        return InMemoryRegisteredClientRepository(client)
//    }
//
//    @Bean
//    fun jwkSource(): JWKSource<SecurityContext?>? {
//        val keyPair: KeyPair = generateRsaKey()
//        val publicKey: RSAPublicKey = keyPair.public as RSAPublicKey
//        val privateKey: RSAPrivateKey = keyPair.private as RSAPrivateKey
//        val rsaKey: RSAKey = RSAKey.Builder(publicKey)
//            .privateKey(privateKey)
//            .keyID(UUID.randomUUID().toString())
//            .build()
//        val jwkSet = JWKSet(rsaKey)
//        return ImmutableJWKSet<SecurityContext?>(jwkSet)
//    }
//
//    private fun generateRsaKey(): KeyPair {
//        val keyPair: KeyPair = try {
//            val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
//            keyPairGenerator.initialize(2048)
//            keyPairGenerator.generateKeyPair()
//        } catch (ex: Exception) {
//            throw IllegalStateException(ex)
//        }
//        return keyPair
//    }
//
//    @Bean
//    fun jwtDecoder(jwkSource: JWKSource<SecurityContext?>?): JwtDecoder? {
//        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
//    }
//
//    @Bean
//    fun authorizationServerSettings(): AuthorizationServerSettings? {
//        return AuthorizationServerSettings.builder().build()
//    }
}

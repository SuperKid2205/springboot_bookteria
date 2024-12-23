package com.hung.practice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.hung.practice.constant.RequestEndpoint;
import com.hung.practice.enums.UserRoles;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecurityConfig {

    final CustomJwtDecoder jwtDecoder;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        // Define endpoint which will authenticated
        httpSecurity.authorizeHttpRequests(
                request -> request
                        // Non-authenticate
                        .requestMatchers(HttpMethod.POST, RequestEndpoint.USER)
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, RequestEndpoint.AUTH + "/**")
                        .permitAll()

                        // ADMIN
                        .requestMatchers(HttpMethod.DELETE, RequestEndpoint.USER)
                        .hasRole(UserRoles.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, RequestEndpoint.USER)
                        .hasRole(UserRoles.ADMIN.name())
                        .requestMatchers(RequestEndpoint.ROLE)
                        .hasRole(UserRoles.ADMIN.name())
                        .requestMatchers(RequestEndpoint.PERMISSION)
                        .hasRole(UserRoles.ADMIN.name())

                        // USER

                        // Authenticate
                        .anyRequest()
                        .authenticated()

                // .requestMatchers(HttpMethod.GET, "/users/**").hasRole(UserRoles.USER.name())

                );

        // Verify token
        httpSecurity.oauth2ResourceServer(
                oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(jwtDecoder) // Verify token
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())) // Convert role string
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint()) // Navigate token expired error
                        .accessDeniedHandler(new JwtAccessDeniedHandler()) // Navigate authorizeHttpRequests error
                );

        // Disable CSRF
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    /**
     * Add User permission to Admin
     */
    @Bean
    RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role("ADMIN")
                .implies("USER")
                .build();
    }

    /**
     * Convert "SCOPE" to "ROLE"
     * @return JwtAuthenticationConverter
     */
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter granted = new JwtGrantedAuthoritiesConverter();
        granted.setAuthorityPrefix("");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(granted);
        return converter;
    }
}

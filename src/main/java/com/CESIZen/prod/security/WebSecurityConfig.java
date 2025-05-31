package com.CESIZen.prod.security;

import com.CESIZen.prod.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;
    @Autowired
    private AuthEntryPointJwt authEntryPointJwt;
    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsService userDetailsService;
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(jwtUtils,userDetailsService);
    }
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Updated configuration for Spring Security 6.x
        http
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults())
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(unauthorizedHandler)
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests

                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                                .requestMatchers("/auth/admin/**").hasRole("ADMIN")
                                .requestMatchers("/auth/**").permitAll()

                                .requestMatchers(HttpMethod.GET,"/diagnostic").permitAll()
                                .requestMatchers("/diagnostic/admin/**").hasRole("ADMIN")
                                .requestMatchers("/diagnostic/ranges/**").hasRole("ADMIN")

                                .requestMatchers(HttpMethod.GET, "/resources").permitAll()
                                .requestMatchers(HttpMethod.GET, "/resources/*").permitAll()
                                .requestMatchers(HttpMethod.POST, "/resources").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PATCH, "/resources/*").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/resources/*").hasRole("ADMIN")

                                .requestMatchers("/users/me", "/users/me/**").authenticated()
                                .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PATCH, "/users/*/deactivate").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PATCH, "/users/*/activate").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/users/*").hasRole("ADMIN")

                                .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPointJwt)
                        .accessDeniedHandler(accessDeniedHandler)
                );
        // Add the JWT Token filter before the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
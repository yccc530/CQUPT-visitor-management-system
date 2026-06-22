package edu.cqupt.visitor.config;

import edu.cqupt.visitor.security.JsonAccessDeniedHandler;
import edu.cqupt.visitor.security.JsonAuthenticationEntryPoint;
import edu.cqupt.visitor.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JsonAuthenticationEntryPoint authenticationEntryPoint;
    private final JsonAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          JsonAuthenticationEntryPoint authenticationEntryPoint,
                          JsonAccessDeniedHandler accessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/auth/logout", "/api/auth/me", "/api/auth/menus").authenticated()

                        .requestMatchers("/api/sys-users/**", "/api/sys-roles/**", "/api/sys-permissions/**",
                                "/api/sys-user-roles/**", "/api/sys-role-permissions/**",
                                "/api/departments/**", "/api/campus-gates/**", "/api/dict-types/**",
                                "/api/dict-items/**", "/api/blacklists/**", "/api/notices/**",
                                "/api/operation-logs/**", "/api/screenshot-records/**", "/api/report-records/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/visitors/**", "/api/visitor-vehicles/**", "/api/visitor-companions/**")
                        .hasAnyRole("ADMIN", "SCHOOL_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/visitors/**", "/api/visitor-vehicles/**", "/api/visitor-companions/**")
                        .hasAnyRole("ADMIN", "VISITOR")
                        .requestMatchers(HttpMethod.PUT, "/api/visitors/**", "/api/visitor-vehicles/**", "/api/visitor-companions/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/visitors/**", "/api/visitor-vehicles/**", "/api/visitor-companions/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/visit-applies/**")
                        .hasAnyRole("ADMIN", "SCHOOL_MANAGER", "HOST", "DEPT_APPROVER")
                        .requestMatchers(HttpMethod.POST, "/api/visit-applies/**")
                        .hasAnyRole("ADMIN", "VISITOR")
                        .requestMatchers(HttpMethod.PUT, "/api/visit-applies/**", "/api/visit-applies/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/visit-applies/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/approval-records/**")
                        .hasAnyRole("ADMIN", "SCHOOL_MANAGER", "HOST", "DEPT_APPROVER")
                        .requestMatchers(HttpMethod.POST, "/api/approval-records/**")
                        .hasAnyRole("ADMIN", "HOST", "DEPT_APPROVER")
                        .requestMatchers(HttpMethod.PUT, "/api/approval-records/**")
                        .hasAnyRole("ADMIN", "HOST", "DEPT_APPROVER")
                        .requestMatchers(HttpMethod.DELETE, "/api/approval-records/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/pass-codes/**")
                        .hasAnyRole("ADMIN", "GATE_GUARD", "VISITOR")
                        .requestMatchers("/api/pass-codes/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/access-records/**")
                        .hasAnyRole("ADMIN", "SCHOOL_MANAGER", "GATE_GUARD")
                        .requestMatchers(HttpMethod.POST, "/api/access-records/**")
                        .hasAnyRole("ADMIN", "GATE_GUARD")
                        .requestMatchers(HttpMethod.PUT, "/api/access-records/**")
                        .hasAnyRole("ADMIN", "GATE_GUARD")
                        .requestMatchers(HttpMethod.DELETE, "/api/access-records/**")
                        .hasRole("ADMIN")

                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
package vn.edu.hutech.lms_api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import vn.edu.hutech.lms_api.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api/v1/files/download/**"
                        ).permitAll()
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/quizzes/submit").hasRole("LEARNER")

                        // Allow authenticated users (learners/instructors/admins) to GET quizzes and questions
                        .requestMatchers(HttpMethod.GET, "/api/v1/quizzes/**", "/api/v1/questions/**").authenticated()
                        // Restrict create/update/delete of quizzes/questions to instructors and admins
                        .requestMatchers(HttpMethod.POST, "/api/v1/quizzes/**", "/api/v1/questions/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/quizzes/**", "/api/v1/questions/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/quizzes/**", "/api/v1/questions/**").hasAnyRole("INSTRUCTOR", "ADMIN")

                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/courses/**",
                                "/api/v1/modules/**",
                                "/api/v1/lessons/**",
                                "/api/v1/certificates/**"
                        ).authenticated()
                        .requestMatchers("/api/v1/courses/**", "/api/v1/modules/**", "/api/v1/lessons/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/certificates/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/dashboard/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

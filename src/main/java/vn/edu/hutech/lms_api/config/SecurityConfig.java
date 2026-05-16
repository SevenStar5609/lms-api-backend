package vn.edu.hutech.lms_api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Nhớ có dòng import này
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import vn.edu.hutech.lms_api.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
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

                        // MỚI: Cho phép TẤT CẢ mọi người (đã đăng nhập) được XEM Khóa học, Chương, Bài học
                        .requestMatchers(HttpMethod.GET, "/api/v1/courses/**", "/api/v1/modules/**", "/api/v1/lessons/**").authenticated()

                        // CŨ: Giới hạn quyền THÊM, SỬA, XÓA (POST, PUT, DELETE) chỉ dành cho Giảng viên và Admin
                        .requestMatchers("/api/v1/courses/**", "/api/v1/modules/**", "/api/v1/lessons/**").hasAnyRole("INSTRUCTOR", "ADMIN")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
package vn.edu.hutech.lms_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hệ thống LMS API")
                        .version("1.0.0")
                        .description("Tài liệu API cho Hệ thống Quản lý Học tập Trực tuyến")
                        .contact(new Contact()
                                .name("Backend Developer")
                                .email("admin@hutech.edu.vn")));
    }
}
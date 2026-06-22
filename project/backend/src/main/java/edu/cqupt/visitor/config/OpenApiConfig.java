package edu.cqupt.visitor.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cquptVisitorOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("重庆邮电大学智慧访客预约与出入校管理系统 API")
                        .version("0.1.0")
                        .description("Spring Boot 3 + MyBatis Plus 后端接口文档"));
    }
}

package com.dbtraining.reconx.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI reconxOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ReconX API")
                        .description("Trade Reconciliation Platform API")
                        .version("v1.0"));
    }
}
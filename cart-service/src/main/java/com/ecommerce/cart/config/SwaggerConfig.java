package com.ecommerce.cart.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI cartServiceAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8082");
        devServer.setDescription("Development Server");




        Info info = new Info()
                .title("Cart Service API")
                .version("1.0.0")
                .description("E-Commerce Cart Microservice - Manages shopping cart operations");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}
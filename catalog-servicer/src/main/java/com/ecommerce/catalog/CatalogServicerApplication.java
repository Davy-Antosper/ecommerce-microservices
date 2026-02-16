package com.ecommerce.catalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CatalogServicerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatalogServicerApplication.class, args);
	}

}

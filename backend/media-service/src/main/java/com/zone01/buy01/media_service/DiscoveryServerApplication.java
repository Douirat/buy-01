package com.zone01.buy01.media_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

// https://www.geeksforgeeks.org/java/service-discovery-and-service-registry-in-microservices/
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(DiscoveryServerApplication.class, args);
	}
}
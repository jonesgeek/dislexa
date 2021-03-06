package com.jonesgeeks.dislexa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@ComponentScan({"com.jonesgeeks.dislexa.config"})
public class DislexaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DislexaApplication.class, args);
	}
}

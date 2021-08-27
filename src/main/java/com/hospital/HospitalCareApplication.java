package com.hospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
public class HospitalCareApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(HospitalCareApplication.class, args);
		System.out.println("google");
	}


	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(HospitalCareApplication.class);
	}

}

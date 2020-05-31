package com.hospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@EnableJpaRepositories(basePackages = "com.hospital.repository")
@SpringBootApplication
public class HospitalCareApplication {

	public static void main(String[] args) {
		SpringApplication.run(HospitalCareApplication.class, args);
	}

}

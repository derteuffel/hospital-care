package com.hospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
public class HospitalCareApplication {

	public static void main(String[] args) {
		SpringApplication.run(HospitalCareApplication.class, args);
		System.out.println("google");
	}

}

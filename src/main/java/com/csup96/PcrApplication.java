package com.csup96;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PcrApplication {

	public static void main(String[] args) {
		SpringApplication.run(PcrApplication.class, args);
	}

}

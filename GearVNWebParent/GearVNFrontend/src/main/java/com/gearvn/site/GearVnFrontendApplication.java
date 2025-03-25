package com.gearvn.site;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.gearvn.common.entity")
public class GearVnFrontendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GearVnFrontendApplication.class, args);
	}

}

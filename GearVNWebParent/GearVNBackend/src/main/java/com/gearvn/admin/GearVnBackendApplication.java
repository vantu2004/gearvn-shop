package com.gearvn.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.gearvn.common.entity", "com.gearvn.admin.user"})
public class GearVnBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GearVnBackendApplication.class, args);
	}

}

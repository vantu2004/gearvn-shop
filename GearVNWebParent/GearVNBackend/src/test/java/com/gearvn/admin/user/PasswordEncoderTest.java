package com.gearvn.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {
	@Test
	public void testEncodePassword() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		String password = "vantu";
		String encodedPassword = bCryptPasswordEncoder.encode(password);
		
		System.out.println(encodedPassword);
		
		boolean matches = bCryptPasswordEncoder.matches(password, encodedPassword);
		System.out.println(matches);
		
		assertThat(matches).isTrue();
	}
}

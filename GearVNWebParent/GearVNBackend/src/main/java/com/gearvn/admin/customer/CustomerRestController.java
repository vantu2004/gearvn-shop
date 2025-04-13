package com.gearvn.admin.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerRestController {
	@Autowired
	private CustomerService customerService;

	@PostMapping("/customers/check_duplicate_email")
	public String checkDuplicateEmail(Integer id, String email) {
		return this.customerService.isEmailUnique(id, email) ? "OK" : "Duplicated";
	}
}

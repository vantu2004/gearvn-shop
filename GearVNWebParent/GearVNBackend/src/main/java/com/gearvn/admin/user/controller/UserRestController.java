package com.gearvn.admin.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gearvn.admin.user.service.UserService;
import com.gearvn.common.entity.User;

@RestController
public class UserRestController {
	@Autowired
	private UserService userService;

	@PostMapping("/users/check_duplicate_email")
	public String checkDuplicateEmail(String email) {
		return this.userService.isEmailUnique(email) ? "OK" : "Duplicated";
	}

	@PostMapping("/users/check_duplicate_email_update")
	public String checkDuplicateEmailUpdate(Integer id, String email) throws Exception {
		User user = this.userService.getUserById(id);

		// email mới giống email cũ thì vẫn chấp nhận
		if (user.getEmail().equals(email.trim())) {
			return "OK";
		}
		// email mới khác email cũ thì check duplicate lại lần nữa
		else {
			return this.userService.isEmailUnique(email) ? "OK" : "Duplicated";
		}
	}
}

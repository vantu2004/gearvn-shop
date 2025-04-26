package com.gearvn.admin;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping("/")
	public String getHomePage() {
		return "index";
	}

	// đảm bảo sau khi login thì ko quay lại đc trang login trừ khi logout
	@GetMapping("/login")
	public String getLoginPage() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		/*
		 * AnonymousAuthenticationToken được dùng khi người dùng chưa đăng nhập, nhưng
		 * vẫn có một phiên bản authentication ẩn danh. trường hợp người dùng đã login
		 * thì chuyển về home luôn tránh chuyển về login nữa
		 */
		if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
			return "redirect:/";
		}
		return "login";
	}
}

package com.gearvn.site;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.gearvn.common.entity.Category;
import com.gearvn.site.category.CategoryService;

@Controller
public class MainController {

	@Autowired
	private CategoryService categoryService;

	@GetMapping("/")
	public String getHomePage(Model model) {
		List<Category> listNoChildrenCategories = this.categoryService.getListNoChildrenCategories();

		model.addAttribute("listNoChildrenCategories", listNoChildrenCategories);

		return "index";
	}

	// đảm bảo sau khi login thì ko quay lại đc trang login trừ khi logout
	@GetMapping("/login")
	public String getLoginPage() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		/*
		 * AnonymousAuthenticationToken được dùng khi người dùng chưa đăng nhập, nhưng
		 * vẫn có một phiên bản authentication ẩn danh. Điều kiện này có nghĩa là: Nếu
		 * người dùng chưa đăng nhập (ẩn danh), thì chuyển hướng về /, nhưng đáng lẽ chỉ
		 * nên chuyển hướng nếu họ đã đăng nhập.
		 */
		if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
			return "redirect:/";
		}
		return "login";
	} 
}

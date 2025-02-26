package com.gearvn.admin.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryRestController {
	@Autowired
	private CategoryService categoryService;

	@PostMapping("/categories/check_duplicate")
	public String checkDuplicate(Integer id, String name, String alias) {

		return this.categoryService.checkUnique(id, name.trim(), alias.trim());
	}
}

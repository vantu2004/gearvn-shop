package com.gearvn.site;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

}

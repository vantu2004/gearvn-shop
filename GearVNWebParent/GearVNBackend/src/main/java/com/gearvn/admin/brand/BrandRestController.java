package com.gearvn.admin.brand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BrandRestController {
	@Autowired
	private BrandService brandService;
	
	@PostMapping("/brands/check_duplicate")
	public String checkDuplicate(Integer id, String name) {
		return this.brandService.isNameUnique(id, name);
	}
}

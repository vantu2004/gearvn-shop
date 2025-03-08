package com.gearvn.admin.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRestController {
	@Autowired
	private ProductService productService;

	@PostMapping("/products/check_duplicate")
	public String checkDuplicate(Integer id, String name, String alias) {

		return this.productService.checkUnique(id, name.trim(), alias.trim());
	}
}

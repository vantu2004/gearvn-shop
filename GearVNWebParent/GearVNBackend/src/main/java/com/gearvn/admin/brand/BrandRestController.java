package com.gearvn.admin.brand;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gearvn.common.entity.Brand;
import com.gearvn.common.entity.Category;

@RestController
public class BrandRestController {
	@Autowired
	private BrandService brandService;

	@PostMapping("/brands/check_duplicate")
	public String checkDuplicate(Integer id, String name) {
		return this.brandService.isNameUnique(id, name);
	}

	@GetMapping("/brands/{id}/categories")
	public List<Category> getCategoriesByBrand(@PathVariable("id") Integer brandId) {
		Brand brand = this.brandService.getBrandById(brandId);
		try {
			Set<Category> categories = brand.getCategories();
			List<Category> listCategories = new ArrayList<Category>();

			for (Category category : categories) {
				Category cat = Category.builder().id(category.getId()).name(category.getName()).build();
				listCategories.add(cat);
			}

			return listCategories;
		} catch (NoSuchElementException e) {
			// TODO: handle exception
			throw new NoSuchElementException("Could not find any brand with id " + brandId);
		}
	}
}

package com.gearvn.admin.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gearvn.admin.brand.BrandService;
import com.gearvn.admin.category.CategoryService;
import com.gearvn.common.entity.Brand;
import com.gearvn.common.entity.Product;

import jakarta.validation.Valid;

@Controller
public class ProductController {
	@Autowired
	private ProductService productService;

	@Autowired
	private BrandService brandService;
	
	@Autowired
	private CategoryService categoryService;

	@GetMapping("/products")
	public String getProductsFirstPage(Model model) {
		List<Product> listProducts = this.productService.getAllProducts();
		model.addAttribute("listProducts", listProducts);

		return "products/list_products";
	}

	@GetMapping("/products/create")
	public String getCreateProductPage(Model model) {
		List<Brand> listBrands = this.brandService.getAllBrands();
		
		model.addAttribute("product", new Product());
		model.addAttribute("listBrands", listBrands);

		return "products/create_product";
	}
	
	@PostMapping("/products/save")
	public String handleSaveProduct(Model model, @Valid Product product, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		// validation
		if (bindingResult.hasErrors()) {
			List<Brand> listBrands = this.brandService.getAllBrands();
			model.addAttribute("listBrands", listBrands);

			return "products/create_product";
		}

//		if (!multipartFile.isEmpty()) {
//			String targetFolder = "../category-images";
//			String fileName = this.uploadImageService.handleSaveUploadFile(multipartFile, targetFolder);
//			category.setImage(fileName);
//		}
//
//		this.categoryService.handleSaveCategory(category);

		redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
		return "redirect:/products";
	}
}

package com.gearvn.site.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.gearvn.common.entity.Category;
import com.gearvn.common.entity.product.Product;
import com.gearvn.site.category.CategoryService;

@Controller
public class ProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private CategoryService categoryService;

	@GetMapping("/c/{category_alias}")
	public String getFirstProductPageByCategory(@PathVariable("category_alias") String categoryAlias, Model model) {
		return getProductPageByCategory(categoryAlias, 1, model);
	}

	@GetMapping("/c/{category_alias}/page/{currentPage}")
	public String getProductPageByCategory(@PathVariable("category_alias") String categoryAlias,
			@PathVariable("currentPage") int currentPage, Model model) {

		try {
			// thay vì lấy category bằng id thì lấy bằng alias và chỉ lấy category đã
			// enabled
			Category category = this.categoryService.getCategoryAliasEnabled(categoryAlias);
			if (category == null) {
				return "error/404";
			}

			// lấy list từ rootCategory --> category hiện tại truyền vào để tạo Breadcrumb
			List<Category> listCategoryParents = this.categoryService.getCategoryParent(category);

			Page<Product> pageableProducts = this.productService.getProductsByCategory(currentPage, category.getId());
			List<Product> listProducts = pageableProducts.getContent();

			int totalPages = pageableProducts.getTotalPages();
			long totalElements = pageableProducts.getTotalElements();

			long startCount = (currentPage - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
			long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
			if (endCount > totalElements) {
				endCount = totalElements;
			}

			long startPage = (currentPage - 2 < 1) ? 1 : currentPage - 2;
			long endPage = (currentPage + 2 > totalPages) ? totalPages : currentPage + 2;

			model.addAttribute("pageTitle", category.getName());
			model.addAttribute("currentCategory", category);
			model.addAttribute("listCategoryParents", listCategoryParents);
			model.addAttribute("listProducts", listProducts);

			// pagination
			model.addAttribute("totalElements", totalElements);
			model.addAttribute("totalPages", totalPages);
			model.addAttribute("currentPage", currentPage);
			model.addAttribute("startPage", startPage);
			model.addAttribute("endPage", endPage);
			model.addAttribute("startCount", startCount);
			model.addAttribute("endCount", endCount);

			return "products/products_by_category";
		} catch (Exception e) {
			return "error/404";
		}
	}

	@GetMapping("/p/{alias}")
	public String getProductByAlias(@PathVariable("alias") String alias, Model model) {
		try {
			Product product = this.productService.getProductByAlias(alias);
			// lấy list từ rootCategory --> product hiện tại truyền vào để tạo Breadcrumb
			List<Category> listCategoryParents = this.categoryService.getCategoryParent(product.getCategory());

			model.addAttribute("product", product);
			model.addAttribute("listCategoryParents", listCategoryParents);

			return "products/product_detail";

		} catch (Exception e) {
			return "error/404";
		}

	}

	@GetMapping("/search")
	public String getSearchResultsFirstPage(@RequestParam(name = "keyword", required = false) String keyword,
			Model model) {
		return getSearchResults(keyword, 1, model);
	}

	@GetMapping("/search/page/{currentPage}")
	public String getSearchResults(@RequestParam(name = "keyword", required = false) String keyword,
			@PathVariable("currentPage") int currentPage, Model model) {

		Page<Product> pageableProducts = this.productService.searchProduct(currentPage, keyword);
		List<Product> listProducts = pageableProducts.getContent();

		int totalPages = pageableProducts.getTotalPages();
		long totalElements = pageableProducts.getTotalElements();

		long startCount = (currentPage - 1) * ProductService.SEARCH_RESULTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.SEARCH_RESULTS_PER_PAGE - 1;
		if (endCount > totalElements) {
			endCount = totalElements;
		}

		long startPage = (currentPage - 2 < 1) ? 1 : currentPage - 2;
		long endPage = (currentPage + 2 > totalPages) ? totalPages : currentPage + 2;

		model.addAttribute("listProducts", listProducts);

		// pagination
		model.addAttribute("totalElements", totalElements);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);

		model.addAttribute("keyword", keyword);

		return "products/product_search";
	}
}

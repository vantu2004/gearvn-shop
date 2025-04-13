package com.gearvn.admin.product;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gearvn.admin.brand.BrandService;
import com.gearvn.admin.category.CategoryService;
import com.gearvn.admin.common.UploadImageService;
import com.gearvn.admin.paging.PagingAndSortingHelper;
import com.gearvn.admin.paging.PagingAndSortingParam;
import com.gearvn.admin.security.GearvnUserDetails;
import com.gearvn.common.entity.Brand;
import com.gearvn.common.entity.Category;
import com.gearvn.common.entity.Product;
import com.gearvn.common.entity.ProductImage;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class ProductController {
	@Autowired
	private ProductService productService;

	@Autowired
	private BrandService brandService;

	@Autowired
	private UploadImageService uploadImageService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductSaveHelper productSaveHelper;

	@GetMapping("/products")
	public String getProductsFirstPage(Model model) {
		// return getProductPage_pageable(1, model, "asc", "name", null, null);
		return "redirect:/products/page/1?sortField=name&sortType=asc";
	}

	@GetMapping("/products/page/{currentPage}")
	public String getProductPage_pageable(Model model, @PathVariable("currentPage") int currentPage,
			@PagingAndSortingParam(listName = "listProducts", moduleUrl = "/products") PagingAndSortingHelper helper,
			@RequestParam(name = "inputSearchCategoryId", required = false) Integer inputSearchCategoryId) {

		this.productService.getAllProduct_pageable(currentPage, helper, inputSearchCategoryId);

		List<Category> categories = this.categoryService.getAllCategories("asc");

		// filter with category
		model.addAttribute("categories", categories);

		// set value search combobox
		if (inputSearchCategoryId != null) {
			model.addAttribute("inputSearchCategoryId", inputSearchCategoryId);
		}

		return "products/list_products";
	}

	@GetMapping("/products/create")
	public String getCreateProductPage(Model model) {
		List<Brand> listBrands = this.brandService.getAllBrands();

		model.addAttribute("product", new Product());
		model.addAttribute("listBrands", listBrands);

		return "products/create_product";
	}

	// xử lý chung create/update
	// required = false nghĩa là tham số ko bắt buộc
	/*
	 * khi user đăng nhập thành công, GearvnUserDetailsService trả về 1 UserDetails,
	 * nhưng UserDetails này đã được custom lại bởi GearvnUserDetails, lúc này
	 * GearvnUserDetails đc lưu vào security context, @AuthenticationPrincipal giúp
	 * lấy GearvnUserDetails đã lưu ra
	 */
	@PostMapping("/products/save")
	public String handleSaveProduct(Model model, @Valid Product product, BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			@RequestParam(name = "multipartFile", required = false) MultipartFile multipartFile,
			@RequestParam(name = "extraMultipartFile", required = false) MultipartFile[] extraMultipartFile,
			@RequestParam(name = "detailIds", required = false) String[] detailIds,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues,
			@RequestParam(name = "extraImageIds", required = false) String[] extraImageIds,
			@RequestParam(name = "extraImageNames", required = false) String[] extraImageNames,
			@AuthenticationPrincipal GearvnUserDetails gearvnUserDetails) {

		// trường hợp Salesperson update cost/price/discountPercent
		if (gearvnUserDetails.hasRole("Salesperson") && !gearvnUserDetails.hasRole("Admin")
				&& !gearvnUserDetails.hasRole("Editor")) {

			return this.productSaveHelper.updateProductBySalesperson(product, model, redirectAttributes);
		}

		// Kiểm tra lỗi validation, nếu có thì quay lại trang tạo sản phẩm
		if (bindingResult.hasErrors()) {
			model.addAttribute("listBrands", this.brandService.getAllBrands());
			return "products/create_product";
		}

		// Nếu sản phẩm đã tồn tại (có ID), lấy sản phẩm từ database
		Product existingProduct = product.getId() != null ? productService.getProductById(product.getId()) : null;
		// Thư mục lưu ảnh sản phẩm
		String targetFolder = "../product-images";

		this.productSaveHelper.handleMainImage(multipartFile, targetFolder, product, existingProduct);
		/*
		 * lúc này tập images/productDetails trong product rỗng --> bản chất 2 hàm dưới
		 * là duyệt và add vào images/productDetails --> việc dùng orphanRemoval = true
		 * khiến cho bất kỳ row giá trị nào trước đó trong images/productDetails ko còn
		 * đc tham chiếu sẽ tự động xóa khỏi db
		 */
		this.productSaveHelper.handleExtraImages(extraMultipartFile, extraImageIds, extraImageNames, targetFolder,
				product, existingProduct);
		this.productSaveHelper.setProductDetails(detailIds, detailNames, detailValues, product);

		productService.handleSaveProduct(product);

		redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
		return "redirect:/products";
	}

	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
		try {
			String targetFolder = "../product-images";

			Product product = this.productService.getProductById(id);
			if (product != null) {
				String mainImage = product.getMainImage();
				Set<ProductImage> productImages = product.getImages();

				this.productService.deleteProductById(id);

				/*
				 * nên để xóa ảnh sau vì trường hợp xóa category thất bại thì ném ngoại lệ trc
				 * thay vì xóa ảnh
				 */
				// xóa cả ảnh chính và phụ
				this.uploadImageService.deletePhotos(targetFolder, mainImage);
				for (ProductImage productImage : productImages) {
					this.uploadImageService.deletePhotos(targetFolder, productImage.getName());
				}
			}

			redirectAttributes.addFlashAttribute("message", "The product ID " + id + " has been deleted successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}

		return "redirect:/products";
	}

	@GetMapping("/products/update/{id}")
	public String getUpdateProductPage(@PathVariable("id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			System.out.println("im here");

			Product product = this.productService.getProductById(id);
			List<Brand> listBrands = this.brandService.getAllBrands();

			model.addAttribute("product", product);
			model.addAttribute("listBrands", listBrands);

			return "products/create_product";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";
		}
	}

	@GetMapping("/products/detail/{id}")
	public String getDetailsProduct(@PathVariable("id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {

		try {
			Product product = this.productService.getProductById(id);
			model.addAttribute("product", product);

			return "products/view_product";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";
		}
	}

	@GetMapping("/products/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<Product> listProducts = this.productService.getAllProducts();
		ProductCsvExporter exporter = new ProductCsvExporter();
		exporter.export(listProducts, response);
	}
}

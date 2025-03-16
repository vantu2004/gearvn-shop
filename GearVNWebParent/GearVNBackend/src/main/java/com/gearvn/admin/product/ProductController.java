package com.gearvn.admin.product;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.gearvn.admin.common.UploadImageService;
import com.gearvn.common.entity.Brand;
import com.gearvn.common.entity.Product;
import com.gearvn.common.entity.ProductImage;

import jakarta.validation.Valid;

@Controller
public class ProductController {
	@Autowired
	private ProductService productService;

	@Autowired
	private BrandService brandService;

	@Autowired
	private UploadImageService uploadImageService;

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
			RedirectAttributes redirectAttributes, @RequestParam("multipartFile") MultipartFile multipartFile,
			@RequestParam("extraMultipartFile") MultipartFile[] extraMultipartFile) {
		// validation
		if (bindingResult.hasErrors()) {
			List<Brand> listBrands = this.brandService.getAllBrands();
			model.addAttribute("listBrands", listBrands);

			return "products/create_product";
		}
		
		String targetFolder = "../product-images";
		
		// main image
		if (!multipartFile.isEmpty()) {
			String fileName = this.uploadImageService.handleSaveUploadFile(multipartFile, targetFolder);
			product.setMainImage(fileName);
		}
		
		// extra images
		if (extraMultipartFile.length > 0) {
			for (MultipartFile mult : extraMultipartFile) {
				if (!StringUtils.isEmpty(mult.getOriginalFilename())) {
					String fileName = this.uploadImageService.handleSaveUploadFile(mult, targetFolder);
					product.addExtraImages(fileName);
				}
			}
		}

		this.productService.handleSaveProduct(product);

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
				this.uploadImageService.deletePhotos(targetFolder, mainImage);
				for (ProductImage productImage : productImages) {
					this.uploadImageService.deletePhotos(targetFolder, productImage.getName());
				}
			}

			redirectAttributes.addFlashAttribute("message", "The product ID " + id + " has been deleted successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "Could not find any product with id " + id);
		}

		return "redirect:/products";
	}
}

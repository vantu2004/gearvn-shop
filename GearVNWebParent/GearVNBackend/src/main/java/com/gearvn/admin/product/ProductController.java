package com.gearvn.admin.product;

import java.util.Arrays;
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

	// xử lý chung create/update
	// required = false nghĩa là tham số ko bắt buộc
	@PostMapping("/products/save")
	public String handleSaveProduct(Model model, @Valid Product product, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, @RequestParam("multipartFile") MultipartFile multipartFile,
			@RequestParam(name = "extraMultipartFile", required = false) MultipartFile[] extraMultipartFile,
			@RequestParam(name = "detailIds", required = false) String[] detailIds,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues,
			@RequestParam(name = "extraImageIds", required = false) String[] extraImageIds,
			@RequestParam(name = "extraImageNames", required = false) String[] extraImageNames) {

		// Kiểm tra lỗi validation, nếu có thì quay lại trang tạo sản phẩm
		if (bindingResult.hasErrors()) {
			model.addAttribute("listBrands", this.brandService.getAllBrands());
			return "products/create_product";
		}

		// Nếu sản phẩm đã tồn tại (có ID), lấy sản phẩm từ database
		Product existingProduct = product.getId() != null ? productService.getProductById(product.getId()) : null;
		// Thư mục lưu ảnh sản phẩm
		String targetFolder = "../product-images";

		handleMainImage(multipartFile, targetFolder, product, existingProduct);
		/*
		 * lúc này tập images/productDetails trong product rỗng --> bản chất 2 hàm dưới
		 * là duyệt và add vào images/productDetails --> việc dùng orphanRemoval = true
		 * khiến cho bất kỳ row giá trị nào trước đó trong images/productDetails ko còn
		 * đc tham chiếu sẽ tự động xóa khỏi db
		 */
		handleExtraImages(extraMultipartFile, extraImageIds, extraImageNames, targetFolder, product, existingProduct);
		setProductDetails(detailIds, detailNames, detailValues, product);

		productService.handleSaveProduct(product);

		redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
		return "redirect:/products";
	}

	// Xử lý ảnh chính của sản phẩm
	private void handleMainImage(MultipartFile multipartFile, String targetFolder, Product product,
			Product existingProduct) {
		// Nếu không có ảnh thì không làm gì cả
		if (multipartFile.isEmpty())
			return;

		// Nếu sản phẩm đã có ảnh chính, xóa ảnh cũ trước khi cập nhật ảnh mới
		if (existingProduct != null) {
			uploadImageService.deletePhotos(targetFolder, existingProduct.getMainImage());
		}
		// Lưu ảnh mới và cập nhật vào sản phẩm
		product.setMainImage(uploadImageService.handleSaveUploadFile(multipartFile, targetFolder));
	}

	// Xử lý ảnh phụ của sản phẩm
	private void handleExtraImages(MultipartFile[] extraMultipartFile, String[] extraImageIds, String[] extraImageNames,
			String targetFolder, Product product, Product existingProduct) {

		// Lưu các ảnh phụ được upload lên
		if (extraMultipartFile != null) {
			for (MultipartFile multipartFile : extraMultipartFile) {
				// Bỏ qua file rỗng
				if (!StringUtils.isBlank(multipartFile.getOriginalFilename())) {
					String fileName = this.uploadImageService.handleSaveUploadFile(multipartFile, targetFolder);
					product.addExtraImages(fileName);
				}
			}
		}

		// Thêm ảnh phụ đã có sẵn (không phải ảnh mới tải lên)
		if (extraImageIds != null && extraImageNames != null) {
			for (String name : extraImageNames) {
				product.addExtraImages(name);
			}
		}

		// Nếu sản phẩm đã tồn tại, xóa các ảnh phụ bị loại bỏ
		if (existingProduct != null) {
			deleteRemovedImages(extraImageIds, existingProduct, targetFolder);
		}
	}

	// Xóa các ảnh phụ bị loại bỏ khi cập nhật sản phẩm
	private void deleteRemovedImages(String[] extraImageIds, Product existingProduct, String targetFolder) {
		for (ProductImage productImage : existingProduct.getImages()) {
			if (extraImageIds == null || !Arrays.asList(extraImageIds).contains(productImage.getId().toString())) {
				this.uploadImageService.deletePhotos(targetFolder, productImage.getName());
			}
		}
	}

	// Cập nhật thông tin chi tiết của sản phẩm
	private void setProductDetails(String[] detailIds, String[] detailNames, String[] detailValues, Product product) {
		// Nếu không có chi tiết sản phẩm thì thoát
		if (detailNames == null || detailValues == null)
			return;

		for (int i = 0; i < detailNames.length; i++) {
			if (!StringUtils.isBlank(detailNames[i]) && !StringUtils.isBlank(detailValues[i])) {
				// Nếu có ID, nghĩa là đang cập nhật thông tin chi tiết
				if (!detailIds[i].equals("0")) {
					product.addProductDetail(detailIds[i], detailNames[i], detailValues[i]);
				}
				// Nếu ID là "0", nghĩa là thêm mới thông tin chi tiết
				else {
					product.addProductDetail(detailNames[i], detailValues[i]);
				}
			}
		}
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
			redirectAttributes.addFlashAttribute("message", "Could not find any product with id " + id);
		}

		return "redirect:/products";
	}

	@GetMapping("/products/update/{id}")
	public String getUpdateProductPage(@PathVariable("id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			Product product = this.productService.getProductById(id);
			List<Brand> listBrands = this.brandService.getAllBrands();

			model.addAttribute("product", product);
			model.addAttribute("listBrands", listBrands);

			return "products/create_product";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "Could not find any product with id " + id);
			return "redirect:/propducts";
		}
	}
}

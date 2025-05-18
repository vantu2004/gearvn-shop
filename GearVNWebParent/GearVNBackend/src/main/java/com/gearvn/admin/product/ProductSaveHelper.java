package com.gearvn.admin.product;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gearvn.admin.brand.BrandService;
import com.gearvn.admin.common.UploadImageService;
import com.gearvn.common.entity.product.Product;
import com.gearvn.common.entity.product.ProductImage;

import jakarta.validation.Valid;

@Service
public class ProductSaveHelper {

	@Autowired
	private ProductService productService;

	@Autowired
	private BrandService brandService;

	@Autowired
	private UploadImageService uploadImageService;

	public String updateProductBySalesperson(@Valid Product product, Model model, RedirectAttributes redirectAttributes,
			boolean isReadOnlyForSalesperson) {
		// nếu 1 trong 3 field đc update <= 0 thì chuyển về trang update
		Product p = this.productService.getProductById(product.getId());
		if (product.getCost() <= 0 || product.getPrice() <= 0 || product.getDiscountPercent() < 0) {

			String costError = product.getCost() <= 0 ? "The value of cost must be positive." : null;
			String priceError = product.getPrice() <= 0 ? "The value of price must be positive." : null;
			String discountError = product.getDiscountPercent() < 0
					? "The value of discount percent must be greater than equal to 0."
					: null;

			model.addAttribute("product", p);

			model.addAttribute("costError", costError);
			model.addAttribute("priceError", priceError);
			model.addAttribute("discountError", discountError);

			model.addAttribute("listBrands", brandService.getAllBrands());
			
			model.addAttribute("isReadOnlyForSalesperson", isReadOnlyForSalesperson);

			return "products/create_product";
		}
		this.productService.saveProductBySalesperson(product);
		redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
		return "redirect:/products";
	}

	// Xử lý ảnh chính của sản phẩm
	public void handleMainImage(MultipartFile multipartFile, String targetFolder, Product product,
			Product existingProduct) {
		// Nếu không có ảnh thì không làm gì cả
		if (multipartFile.isEmpty())
			return;

		// Nếu sản phẩm đã có ảnh chính, xóa ảnh cũ trước khi cập nhật ảnh mới
		if (existingProduct != null) {
			this.uploadImageService.deletePhotos(targetFolder, existingProduct.getMainImage());
		}
		// Lưu ảnh mới và cập nhật vào sản phẩm
		product.setMainImage(this.uploadImageService.handleSaveUploadFile(multipartFile, targetFolder));
	}

	// Xử lý ảnh phụ của sản phẩm
	public void handleExtraImages(MultipartFile[] extraMultipartFile, String[] extraImageIds, String[] extraImageNames,
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
	public void deleteRemovedImages(String[] extraImageIds, Product existingProduct, String targetFolder) {
		for (ProductImage productImage : existingProduct.getImages()) {
			if (extraImageIds == null || !Arrays.asList(extraImageIds).contains(productImage.getId().toString())) {
				this.uploadImageService.deletePhotos(targetFolder, productImage.getName());
			}
		}
	}

	// Cập nhật thông tin chi tiết của sản phẩm
	public void setProductDetails(String[] detailIds, String[] detailNames, String[] detailValues, Product product) {
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
}

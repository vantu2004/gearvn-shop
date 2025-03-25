package com.gearvn.site.product;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.gearvn.common.entity.Product;

@Service
public class ProductService {

	public static int PRODUCTS_PER_PAGE = 12;
	public static int SEARCH_RESULTS_PER_PAGE = 12;

	@Autowired
	private ProductRepository productRepository;

	// lấy tất cả sản phẩm đã enabled có categoryId truyền vào (kể cả sản phẩm trong
	// subCategory)
	public Page<Product> getProductsByCategory(int currentPage, Integer categoryId) {
		String categoryIdMatch = "-" + categoryId.toString() + "-";

		Pageable pageable = PageRequest.of(currentPage - 1, PRODUCTS_PER_PAGE);
		Page<Product> pageableProducts = this.productRepository.findProductsByCategory(categoryId, categoryIdMatch,
				pageable);

		return pageableProducts;
	}

	public Product getProductByAlias(String alias) {
		Product product = this.productRepository.findByAlias(alias);
		if (product != null) {
			return product;
		}
		throw new NoSuchElementException("Could not find any product with alias " + alias);
	}
	
	public Page<Product> searchProduct(int currentPage, String keyword){
		Pageable pageable = PageRequest.of(currentPage - 1, SEARCH_RESULTS_PER_PAGE);
		
		return this.productRepository.findProductsEnabledByKeyword(keyword, pageable);
	}

}

package com.gearvn.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.gearvn.common.entity.Product;

import jakarta.validation.Valid;

@Service
public class ProductService {
	public static int PRODUCTS_PER_PAGE = 5;
	@Autowired
	private ProductRepository productRepository;

	public List<Product> getAllProducts() {
		return this.productRepository.findAll();
	}

	public Page<Product> getAllProduct_pageable(int currentPage, String sortField, String sortType, String keyword,
			Integer inputSearchCategoryId) {
		Sort sort = Sort.by(sortField);
		sort = sortType.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(currentPage - 1, PRODUCTS_PER_PAGE, sort);

		if (!StringUtils.isBlank(keyword)) {
			if (inputSearchCategoryId != null && inputSearchCategoryId > 0) {
				String categoryIdMatch = "-" + inputSearchCategoryId.toString() + "-";
				return this.productRepository.findAll(inputSearchCategoryId, categoryIdMatch, keyword, pageable);
			}
			return this.productRepository.findAll(keyword, pageable);
		}

		/*
		 * nếu để findAll khi keyword null/rỗng trong if trên thì nó sẽ chạy vào if trên
		 * trc tiên thay vì chạy vào if này
		 */
		if (inputSearchCategoryId != null && inputSearchCategoryId > 0) {
			String categoryIdMatch = "-" + inputSearchCategoryId.toString() + "-";
			return this.productRepository.findAll(inputSearchCategoryId, categoryIdMatch, pageable);
		}

		return this.productRepository.findAll(pageable);
	}

	public void handleSaveProduct(@Valid Product product) {
		if (product.getId() == null) {
			product.setCreatedTime(new Date());
		}

		product.setUpdatedTime(new Date());

		String defaultAlias = product.getAlias().toLowerCase().replaceAll(" ", "-");
		product.setAlias(defaultAlias);

		this.productRepository.save(product);
	}

	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreatingNew = (id == null || id == 0);

		Product productByName = this.productRepository.findByName(name);

		// create
		if (isCreatingNew) {
			if (productByName != null) {
				return "DuplicateName";
			} else {
				Product productByAlias = this.productRepository.findByAlias(alias);
				if (productByAlias != null) {
					return "DuplicateAlias";
				}
			}
		}
		// update
		else {
			if (productByName != null && productByName.getId() != id) {
				return "DuplicateName";
			}

			Product productByAlias = this.productRepository.findByAlias(alias);
			if (productByAlias != null && productByAlias.getId() != id) {
				return "DuplicateAlias";
			}

		}

		return "OK";
	}

	public Product getProductById(Integer id) {
		Product product = this.productRepository.findById(id).orElse(null);
		if (product != null) {
			return product;
		}
		throw new NoSuchElementException("Could not find any product with id " + id);
	}

	public void deleteProductById(Integer id) {
		Long count = this.productRepository.countById(id);
		if (count == 0 || count == null) {
			throw new NoSuchElementException("Could not find any product with id " + id);
		}

		this.productRepository.deleteById(id);
	}

	// hàm xử lý lưu cót/listPrice/discount của role Salesperson vì role này chỉ cho
	// update 3 thông tin đó
	public void saveProductBySalesperson(Product product) {
		Product productInDb = this.productRepository.findById(product.getId()).orElse(null);
		if (productInDb != null) {
			productInDb.setCost(product.getCost());
			productInDb.setPrice(product.getPrice());
			productInDb.setDiscountPercent(product.getDiscountPercent());

			productInDb.setUpdatedTime(new Date());
			
			this.productRepository.save(productInDb);
		}
	}
}

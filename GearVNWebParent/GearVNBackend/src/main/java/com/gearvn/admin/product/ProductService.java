package com.gearvn.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.gearvn.admin.paging.PagingAndSortingHelper;
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

	public void getAllProduct_pageable(int currentPage, PagingAndSortingHelper helper, Integer inputSearchCategoryId) {
		Pageable pageable = helper.createPageable(currentPage, PRODUCTS_PER_PAGE);
		System.out.println("Keyword: " + helper.getKeyword());
		Page<Product> page = null;

		if (!StringUtils.isBlank(helper.getKeyword())) {
			if (inputSearchCategoryId != null && inputSearchCategoryId > 0) {
				String categoryIdMatch = "-" + inputSearchCategoryId.toString() + "-";
				page = this.productRepository.findAll(inputSearchCategoryId, categoryIdMatch, helper.getKeyword(),
						pageable);
			} else {
				page = this.productRepository.findAll(helper.getKeyword(), pageable);
			}
		} else {
			if (inputSearchCategoryId != null && inputSearchCategoryId > 0) {
				String categoryIdMatch = "-" + inputSearchCategoryId.toString() + "-";
				page = this.productRepository.findAll(inputSearchCategoryId, categoryIdMatch, pageable);
			} else {
				page = this.productRepository.findAll(pageable);
			}
		}
		
		helper.updateModelAttributes(currentPage, page);
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

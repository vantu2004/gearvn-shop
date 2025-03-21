package com.gearvn.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gearvn.common.entity.Product;

import jakarta.validation.Valid;

@Service
public class ProductService {
	@Autowired
	private ProductRepository productRepository;

	public List<Product> getAllProducts() {
		return this.productRepository.findAll();
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
}

package com.gearvn.site.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gearvn.common.entity.CartItem;
import com.gearvn.common.entity.Customer;
import com.gearvn.common.entity.Product;

@Service
public class CartItemService {

	@Autowired
	private CartItemRepository cartItemRepository;

	public void handleAddProductToCart(Customer customer, Integer productId, int quantity) {
		Product product = Product.builder().id(productId).build();

		CartItem cartItem = this.cartItemRepository.findByCustomerAndProduct(customer, product);
		if (cartItem != null) {
			cartItem.setQuantity(cartItem.getQuantity() + quantity);
		} else {
			cartItem = new CartItem();
			cartItem.setCustomer(customer);
			cartItem.setProduct(product);
			cartItem.setQuantity(quantity);
		}

		this.cartItemRepository.save(cartItem);
	}
}

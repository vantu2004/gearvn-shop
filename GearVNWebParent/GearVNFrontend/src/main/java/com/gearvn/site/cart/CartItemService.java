package com.gearvn.site.cart;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gearvn.common.entity.CartItem;
import com.gearvn.common.entity.Customer;
import com.gearvn.common.entity.product.Product;
import com.gearvn.site.product.ProductRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CartItemService {

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private ProductRepository productRepository;

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

	public List<CartItem> getAllCartItems(Customer customer) {
		return this.cartItemRepository.findByCustomer(customer);
	}

	// trả về subTotal(tổng tiền 1 loại sản phẩm)
	public float updateQuantity(Customer customer, Integer productId, int quantity) {
		this.cartItemRepository.updateQuantity(customer.getId(), productId, quantity);

		Product product = this.productRepository.findById(productId).get();

		return product.getDiscountPrice() * quantity;
	}

	public void deleteCartItem(Customer customer, Integer productId) {
		this.cartItemRepository.deleteCartItem(customer.getId(), productId);
	}

	public void deleteCartItemByCustomer(Customer customer) {
		this.cartItemRepository.deleteCartByCustomer(customer.getId());
	}
}

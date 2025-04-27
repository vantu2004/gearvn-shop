package com.gearvn.site.cart;

import java.util.NoSuchElementException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gearvn.common.entity.Customer;
import com.gearvn.site.Utility;
import com.gearvn.site.customer.CustomerService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class CartRestController {

	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private CustomerService customerService;

	@PostMapping("/cart/add/{productId}/{quantity}")
	public String addProductToCart(@PathVariable("productId") Integer productId, @PathVariable("quantity") int quantity,
			HttpServletRequest request) {

		try {
			Customer customer = this.getAuthenticatedCustomer(request);
			this.cartItemService.handleAddProductToCart(customer, productId, quantity);

			return "Added product to cart successfully.";
		} catch (Exception e) {
			// TODO: handle exception
			return "You must login to add this product to cart.";
		}
	}

	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		if (!StringUtils.isBlank(email)) {
			return this.customerService.getCustomerByEmail(email);
		}

		throw new NoSuchElementException("No authenticated customer.");
	}
}

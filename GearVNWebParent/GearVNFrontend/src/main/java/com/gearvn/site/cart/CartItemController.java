package com.gearvn.site.cart;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.gearvn.common.entity.CartItem;
import com.gearvn.common.entity.Customer;
import com.gearvn.site.Utility;
import com.gearvn.site.customer.CustomerService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CartItemController {

	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private CustomerService customerService;

	@GetMapping("/cart")
	public String getCartItemPage(Model model, HttpServletRequest request) {
		Customer customer = this.getAuthenticatedCustomer(request);

		List<CartItem> cartItems = this.cartItemService.getAllCartItems(customer);
		float totalPrice = 0;
		for (CartItem cartItem : cartItems) {
			totalPrice += cartItem.getTotalPrice();
		}
		
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("totalPrice", totalPrice);

		return "cart/cart";
	}

	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);

		return this.customerService.getCustomerByEmail(email);
	}
}

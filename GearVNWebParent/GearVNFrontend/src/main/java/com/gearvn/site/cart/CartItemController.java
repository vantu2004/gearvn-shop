package com.gearvn.site.cart;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.gearvn.common.entity.Address;
import com.gearvn.common.entity.CartItem;
import com.gearvn.common.entity.Customer;
import com.gearvn.common.entity.ShippingRate;
import com.gearvn.site.Utility;
import com.gearvn.site.address.AddressService;
import com.gearvn.site.customer.CustomerService;
import com.gearvn.site.shipping.ShippingRateService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CartItemController {

	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private AddressService addressService;

	@Autowired
	private ShippingRateService shippingRateService;

	@GetMapping("/cart")
	public String getCartItemPage(Model model, HttpServletRequest request) {
		Customer customer = this.getAuthenticatedCustomer(request);

		List<CartItem> cartItems = this.cartItemService.getAllCartItems(customer);
		float totalPrice = 0;
		for (CartItem cartItem : cartItems) {
			totalPrice += cartItem.getTotalPrice();
		}

		// xét trường hợp customer chọn/ko chọn địa chỉ mặc định (primaryAddress)
		Address defaultAddress = this.addressService.getDefaultAddress(customer);
		ShippingRate shippingRate = null;
		boolean usePrimaryAddressAsDefault = false;
		if (defaultAddress != null) {
			shippingRate = this.shippingRateService.getShippingRateForAddress(defaultAddress);
		} else {
			usePrimaryAddressAsDefault = true;
			shippingRate = this.shippingRateService.getShippingRateForCustomer(customer);
		}

		model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
		// truyền vào true/false trong trường hợp có/ko có tồn tại shippingRate
		model.addAttribute("shippingRate", shippingRate != null);
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("totalPrice", totalPrice);

		return "cart/cart";
	}

	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);

		return this.customerService.getCustomerByEmail(email);
	}
}

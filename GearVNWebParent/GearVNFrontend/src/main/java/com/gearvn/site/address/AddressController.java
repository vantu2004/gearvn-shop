package com.gearvn.site.address;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gearvn.common.entity.Address;
import com.gearvn.common.entity.Country;
import com.gearvn.common.entity.Customer;
import com.gearvn.site.Utility;
import com.gearvn.site.customer.CustomerService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class AddressController {

	@Autowired
	private AddressService addressService;

	@Autowired
	private CustomerService customerService;

	@GetMapping("/address_book")
	public String getAddressBookPage(Model model, HttpServletRequest request) {
		Customer customer = this.getAuthenticatedCustomer(request);
		List<Address> addresses = this.addressService.getAllAddressBookByCustomer(customer);

		boolean usePrimaryAddressAsDefault = true;
		for (Address address : addresses) {
			if (address.isDefaultForShipping()) {
				usePrimaryAddressAsDefault = false;
				break;
			}
		}

		model.addAttribute("addresses", addresses);
		model.addAttribute("customer", customer);
		model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);

		return "address_book/list_addresses";
	}

	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);

		return this.customerService.getCustomerByEmail(email);
	}

	@GetMapping("/address_book/create")
	public String getCreateAddressBookPage(Model model) {
		List<Country> countries = customerService.getAllCountries();

		model.addAttribute("countries", countries);
		model.addAttribute("address", new Address());

		return "address_book/create_address_book";
	}

	@PostMapping("/address_book/save")
	public String handleSaveAddressBook(@Valid Address address, BindingResult bindingResult, HttpServletRequest request,
			Model model, RedirectAttributes redirectAttributes) {

		// validation
		if (bindingResult.hasErrors()) {
			List<Country> countries = customerService.getAllCountries();
			model.addAttribute("countries", countries);

			// vì create và update dùng chung hàm nên đảm bảo đá lỗi về đúng page
			if (address.getId() != null) {
				return "address_book/update_address_book";
			}
			return "address_book/create_address_book";
		}

		Customer customer = this.getAuthenticatedCustomer(request);

		address.setCustomer(customer);
		this.addressService.handleSaveAddress(address);

		/*
		 * xác định chuyển hướng sau khi update (mặc định là về /address_book và ở đây
		 * chọn defaultAddress nào thì sẽ dựa vào tham số trên url để redirect thêm lần
		 * nữa)
		 */
		String redirectOption = request.getParameter("redirect");
		String redirectURL = "redirect:/address_book";

		if ("cart".equals(redirectOption)) {
			redirectURL += "?redirect=cart";
		} else if ("checkout".equals(redirectOption)) {
			redirectURL += "?redirect=checkout";
		}

		redirectAttributes.addFlashAttribute("message", "The address has been saved successfully.");

		return redirectURL;
	}

	@GetMapping("/address_book/update/{id}")
	public String editAddress(@PathVariable("id") Integer addressId, Model model, HttpServletRequest request) {
		Customer customer = this.getAuthenticatedCustomer(request);
		List<Country> countries = customerService.getAllCountries();

		Address address = this.addressService.getAddressByIdAndCustomer(addressId, customer.getId());

		model.addAttribute("address", address);
		model.addAttribute("countries", countries);

		return "address_book/update_address_book";
	}

	@GetMapping("/address_book/delete/{id}")
	public String deleteAddress(@PathVariable("id") Integer addressId, RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		Customer customer = this.getAuthenticatedCustomer(request);
		this.addressService.deleteAddressByIdAndCustomer(addressId, customer.getId());

		redirectAttributes.addFlashAttribute("message", "The address id " + addressId + " has been deleted.");

		return "redirect:/address_book";
	}

	@GetMapping("/address_book/default/{id}")
	public String setDefaultAddress(@PathVariable("id") Integer addressId, HttpServletRequest request) {
		Customer customer = this.getAuthenticatedCustomer(request);
		addressService.setDefaultAddress(addressId, customer.getId());

		String redirectOption = request.getParameter("redirect");
		String redirectURL = "redirect:/address_book";

		if ("cart".equals(redirectOption)) {
			redirectURL = "redirect:/cart";
		} else if ("checkout".equals(redirectOption)) {
			/*
			 * trong trường hợp từ checkout redirect tới page này sau đó click chọn 1
			 * address bất kỳ, nếu address đó đã hỗ trợ shippingRate thì sẽ quay lại page
			 * checkout nhưng nếu ch thì sẽ redirect về page cart nhờ check null
			 * shippingRate trong CheckoutController
			 */
			redirectURL = "redirect:/checkout";
		}

		return redirectURL;
	}
}

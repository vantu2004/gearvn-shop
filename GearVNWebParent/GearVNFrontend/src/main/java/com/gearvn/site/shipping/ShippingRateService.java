package com.gearvn.site.shipping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gearvn.common.entity.Address;
import com.gearvn.common.entity.Customer;
import com.gearvn.common.entity.ShippingRate;

@Service
public class ShippingRateService {

	@Autowired
	private ShippingRateRepository shippingRateRepository;

	public ShippingRate getShippingRateForCustomer(Customer customer) {
		String state = customer.getState();
		if (!StringUtils.hasText(state)) {
			state = customer.getCity();
		}

		return this.shippingRateRepository.findByCountryAndState(customer.getCountry(), state);
	}

	public ShippingRate getShippingRateForAddress(Address address) {
		String state = address.getState();
		if (!StringUtils.hasText(state)) {
			state = address.getCity();
		}

		return this.shippingRateRepository.findByCountryAndState(address.getCountry(), state);
	}
}

package com.gearvn.site.address;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gearvn.common.entity.Address;
import com.gearvn.common.entity.Customer;

import jakarta.transaction.Transactional;

@Service
public class AddressService {

	@Autowired
	private AddressRepository addressRepository;

	public List<Address> getAllAddressBookByCustomer(Customer customer) {
		return this.addressRepository.findByCustomer(customer);
	}

	public void handleSaveAddress(Address address) {
		this.addressRepository.save(address);
	}

	public Address getAddressByIdAndCustomer(Integer addressId, Integer customerId) {
		return this.addressRepository.findByIdAndCustomer(addressId, customerId);
	}

	@Transactional
	public void deleteAddressByIdAndCustomer(Integer addressId, Integer customerId) {
		this.addressRepository.deleteByIdAndCustomer(addressId, customerId);
	}

	@Transactional
	public void setDefaultAddress(Integer defaultAddressId, Integer customerId) {
		// nếu giữ address mặc định thì defaultAddressId là 0
		if (!defaultAddressId.equals(0)) {
			this.addressRepository.setDefaultAddress(defaultAddressId);
		}
		this.addressRepository.setNonDefaultForOthers(defaultAddressId, customerId);
	}
}

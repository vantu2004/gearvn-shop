package com.gearvn.site.address;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.gearvn.common.entity.Address;
import com.gearvn.common.entity.Country;
import com.gearvn.common.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class AddressRepositoryTests {

	@Autowired
	private AddressRepository addressRepository;

	@Test
	public void testAddNew() {
		Integer customerId = 43;
		Integer countryId = 81; // Japan

		Address newAddress = new Address();
		newAddress.setFirstName("Haruki");
		newAddress.setLastName("Tanaka");
		newAddress.setPhoneNumber("08012345678");
		newAddress.setAddressLine1("2-11-3 Meguro");
		newAddress.setAddressLine2("Green Plaza Apt");
		newAddress.setCity("Tokyo");
		newAddress.setState("Tokyo");
		newAddress.setPostalCode("153-0063");
		newAddress.setCustomer(Customer.builder().id(customerId).build());
		newAddress.setCountry(Country.builder().id(countryId).build());

		Address savedAddress = addressRepository.save(newAddress);

		assertThat(savedAddress).isNotNull();
		assertThat(savedAddress.getId()).isGreaterThan(0);
	}

	@Test
	public void testFindByCustomer() {
		Integer customerId = 5;
		List<Address> listAddresses = addressRepository.findByCustomer(Customer.builder().id(customerId).build());

		assertThat(listAddresses.size()).isGreaterThan(0);

		listAddresses.forEach(System.out::println);
	}

	@Test
	public void testFindByIdAndCustomer() {
		Integer addressId = 1;
		Integer customerId = 5;

		Address address = addressRepository.findByIdAndCustomer(addressId, customerId);

		assertThat(address).isNotNull();

		System.out.println(address);
	}

	@Test
	public void testUpdate() {
		Integer addressId = 2;

		Address address = addressRepository.findById(addressId).get();
		address.setDefaultForShipping(true);

		Address updatedAddress = addressRepository.save(address);

		assertThat(updatedAddress.isDefaultForShipping()).isEqualTo(true);
	}

	@Test
	public void testDeleteByIdAndCustomer() {
		Integer addressId = 1;
		Integer customerId = 5;

		addressRepository.deleteByIdAndCustomer(addressId, customerId);

		Address address = addressRepository.findByIdAndCustomer(addressId, customerId);
		assertThat(address).isNull();
	}

	@Test
	public void testSetDefault() {
		Integer addressId = 3;
		addressRepository.setDefaultAddress(addressId);

		Address address = addressRepository.findById(addressId).get();

		assertThat(address.isDefaultForShipping()).isTrue();
	}

	@Test
	public void testSetNonDefaultAddresses() {
		Integer addressId = 6;
		Integer customerId = 43;
		addressRepository.setNonDefaultForOthers(addressId, customerId);
	}

	@Test
	public void testGetDefault() {
		Integer customerId = 7;
		Address address = addressRepository.findDefaultByCustomer(customerId);
		assertThat(address).isNotNull();
		System.out.println(address);
	}
}

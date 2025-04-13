package com.gearvn.admin.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gearvn.admin.paging.SearchPaging;
import com.gearvn.common.entity.Customer;

@Repository
public interface CustomerRepository extends SearchPaging<Customer, Integer> {

	@Query("SELECT c FROM Customer c WHERE CONCAT(" +
	        "c.email, ' ', " +
	        "c.firstName, ' ', " +
	        "c.lastName, ' ', " +
	        "c.addressLine1, ' ', " +
	        "c.addressLine2, ' ', " +
	        "c.city, ' ', " +
	        "c.postalCode, ' ', " +
	        "c.country.name) " +
	        "LIKE %?1%")
	Page<Customer> findAll(String keyword, Pageable pageable);

	Customer findByEmail(String email);

	Long countById(Integer id);

}

package com.gearvn.site.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gearvn.common.entity.AuthenticationType;
import com.gearvn.common.entity.Customer;

import jakarta.transaction.Transactional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

	Customer findByEmail(String email);

	Customer findByVerificationCode(String code);

	/*
	 * mặc định JPA nghĩ truy vấn trong query là SELECT nên trả về
	 * getResultList()/getSingleList() nhưng UPDATE ko trả về --> lỗi --> dùng
	 * 
	 * @Modifying
	 */
	@Modifying
	@Transactional
	@Query("UPDATE Customer c SET c.enabled = true, c.verificationCode = null WHERE c.id = ?1")
	void enabledCustomer(Integer customerId);

	@Modifying
	@Transactional
	@Query("UPDATE Customer c SET c.authenticationType = ?2 WHERE c.id = ?1")
	void updateAuthenticationType(Integer customerId, AuthenticationType authenticationType);

}

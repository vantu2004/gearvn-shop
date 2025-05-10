package com.gearvn.admin.shippingrate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gearvn.admin.paging.SearchPaging;
import com.gearvn.common.entity.ShippingRate;

@Repository
public interface ShippingRateRepository extends SearchPaging<ShippingRate, Integer> {

	@Query("SELECT sr FROM ShippingRate sr WHERE sr.country.id = ?1 AND sr.state = ?2")
	ShippingRate findByCountryAndState(Integer countryId, String state);

	@Query("SELECT sr FROM ShippingRate sr WHERE sr.country.name LIKE %?1% OR sr.state LIKE %?1%")
	Page<ShippingRate> findAll(String keyword, Pageable pageable);

	Long countById(Integer id);
}

package com.gearvn.site.shipping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gearvn.common.entity.Country;
import com.gearvn.common.entity.ShippingRate;

@Repository
public interface ShippingRateRepository extends JpaRepository<ShippingRate, Integer> {

	ShippingRate findByCountryAndState(Country country, String state);
}

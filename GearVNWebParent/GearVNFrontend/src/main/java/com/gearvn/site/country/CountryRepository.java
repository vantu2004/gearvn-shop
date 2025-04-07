package com.gearvn.site.country;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gearvn.common.entity.Country;

public interface CountryRepository extends JpaRepository<Country, Integer> {
	List<Country> findAllByOrderByNameAsc();
}

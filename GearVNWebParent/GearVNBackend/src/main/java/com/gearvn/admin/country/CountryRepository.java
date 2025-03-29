package com.gearvn.admin.country;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gearvn.common.entity.Country;

public interface CountryRepository extends JpaRepository<Country, Integer> {

}

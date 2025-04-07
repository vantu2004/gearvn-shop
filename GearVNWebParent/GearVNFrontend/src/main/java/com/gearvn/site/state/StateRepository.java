package com.gearvn.site.state;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gearvn.common.entity.Country;
import com.gearvn.common.entity.State;

public interface StateRepository extends JpaRepository<State, Integer> {
	List<State> findByCountryOrderByNameAsc(Country country);
}

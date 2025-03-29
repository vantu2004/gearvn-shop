package com.gearvn.admin.setting;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gearvn.common.entity.Currency;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Integer> {

	List<Currency> findAllByOrderByNameAsc();

}

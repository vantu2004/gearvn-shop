package com.gearvn.site.currency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gearvn.common.entity.Currency;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Integer> {

}

package com.gearvn.admin.setting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.gearvn.common.entity.Currency;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CurrencyRepositoryTests {

	@Autowired
	private CurrencyRepository currencyRepository;

	@Test
	public void testCreateCurrencies() {
		List<Currency> listCurrencies = List.of(new Currency("United States Dollar", "$", "USD"),
				new Currency("British Pound", "£", "GPB"), new Currency("Japanese Yen", "¥", "JPY"),
				new Currency("Euro", "€", "EUR"), new Currency("Russian Ruble", "₽", "RUB"),
				new Currency("South Korean Won", "₩", "KRW"), new Currency("Chinese Yuan", "¥", "CNY"),
				new Currency("Brazilian Real", "R$", "BRL"), new Currency("Australian Dollar", "$", "AUD"),
				new Currency("Canadian Dollar", "$", "CAD"), new Currency("Vietnamese đồng ", "₫", "VND"),
				new Currency("Indian Rupee", "₹", "INR"));

		this.currencyRepository.saveAll(listCurrencies);

		List<Currency> currencies = this.currencyRepository.findAll();

		assertThat(currencies).size().isEqualTo(12);
	}

	@Test
	public void testGetAllCurrencies() {
		List<Currency> currencies = this.currencyRepository.findAllByOrderByNameAsc();
		for (Currency currency : currencies) {
			System.out.println(currency);
		}

		assertThat(currencies).size().isGreaterThan(0);
	}
}

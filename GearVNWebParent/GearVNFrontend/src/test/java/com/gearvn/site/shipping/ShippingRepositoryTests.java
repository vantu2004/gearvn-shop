package com.gearvn.site.shipping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.gearvn.common.entity.Country;
import com.gearvn.common.entity.ShippingRate;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ShippingRepositoryTests {

	@Autowired
	private ShippingRateRepository shippingRateRepository;

	@Test
	public void testFindShippingRateByCountryAndState() {
		Country country = Country.builder().id(234).build();
		String state = "New York";

		ShippingRate shippingRate = this.shippingRateRepository.findByCountryAndState(country, state);

		assertThat(shippingRate.getId()).isGreaterThan(0);

		System.out.println(shippingRate);
	}

}

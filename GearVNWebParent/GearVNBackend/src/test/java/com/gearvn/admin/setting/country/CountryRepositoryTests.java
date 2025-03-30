package com.gearvn.admin.setting.country;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.gearvn.common.entity.Country;

// chạy các phần lquan đến jpa (repository) trong môi trường kiểm thử
@DataJpaTest(showSql = false)
// mặc định SpringBoot dùng H2 thay vì db thật --> Replace.NONE đảm bảo dùng db thật
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CountryRepositoryTests {
	@Autowired
	private CountryRepository countryRepository;

	@Test
	public void testCreateCountry() {
		Country country = countryRepository.save(Country.builder().name("China").code("CN").build());
		assertThat(country).isNotNull();
		assertThat(country.getId()).isGreaterThan(0);
	}

	@Test
	public void testListCountries() {
		List<Country> listCountries = countryRepository.findAllByOrderByNameAsc();
		listCountries.forEach(System.out::println);

		assertThat(listCountries.size()).isGreaterThan(0);
	}

	@Test
	public void testUpdateCountry() {
		Integer id = 1;
		String name = "Republic of India";

		Country country = countryRepository.findById(id).get();
		country.setName(name);

		Country updatedCountry = countryRepository.save(country);

		assertThat(updatedCountry.getName()).isEqualTo(name);
	}

	@Test
	public void testGetCountry() {
		Integer id = 1;
		Country country = countryRepository.findById(id).get();
		assertThat(country).isNotNull();
	}

	@Test
	public void testDeleteCountry() {
		Integer id = 1;
		countryRepository.deleteById(id);

		Optional<Country> findById = countryRepository.findById(id);
		assertThat(findById.isEmpty());
	}
}

package com.gearvn.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.intThat;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.gearvn.common.entity.Brand;
import com.gearvn.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTests {
	@Autowired
	private BrandRepository brandRepository;

	@Test
	public void testCreateBrand() {
		Category category4 = Category.builder().id(24).build();
		Category category7 = Category.builder().id(29).build();
		Brand brand = Brand.builder().name("SamSung").logo("LogoGearvn.png").build();
		brand.getCategories().add(category4);
		brand.getCategories().add(category7);

		Brand savedBrand = this.brandRepository.save(brand);

		assertThat(savedBrand).isNotNull();
		assertThat(savedBrand.getId()).isGreaterThan(0);
	}

	@Test
	public void getBrandById() {
		Brand brand = this.brandRepository.findById(2)
				.orElseThrow(() -> new NoSuchElementException("Brand not found!"));

		System.out.println(brand);
	}
	
	@Test
	public void testUpdateBrand() {
		Brand brand = this.brandRepository.findById(3)
				.orElseThrow(() -> new NoSuchElementException("Brand not found!"));
		brand.setName("SamSung Electronics");
		Brand savedBrand = this.brandRepository.save(brand);
		
		assertThat(savedBrand).isNotNull();
		assertThat(savedBrand.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testDeleteBrand() {		
		this.brandRepository.deleteById(2);
	}
}

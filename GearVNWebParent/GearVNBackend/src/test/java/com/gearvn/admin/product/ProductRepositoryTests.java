package com.gearvn.admin.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.gearvn.common.entity.Brand;
import com.gearvn.common.entity.Category;
import com.gearvn.common.entity.Product;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTests {
	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private TestEntityManager testEntityManager;

	@Test
	public void testCreateProduct() {
		Category category = testEntityManager.find(Category.class, 5);
		Brand brand = testEntityManager.find(Brand.class, 37);

//		Product product = Product.builder()
//				.name("Samsung Galaxy A23")
//				.alias("samsung_galaxy_a23")
//				.shortDescription("A good smart phone from SamSung")
//				.fullDescription("This is a good smartphone full description")
//				.brand(brand)
//				.category(category)
//				.price(456)
//				.createdTime(new Date())
//				.updatedTime(new Date())
//				.build();

//		Product product = Product.builder()
//				.name("Dell Inspiron 3000")
//				.alias("dell_inspiron_3000")
//				.shortDescription("Short description for Dell Inspiron 3000")
//				.fullDescription("Full description for Dell Inspiron 3000")
//				.brand(brand)
//				.category(category)
//				.price(456)
//				.cost(456)
//				.enabled(true)
//				.inStock(true)
//				.createdTime(new Date())
//				.updatedTime(new Date())
//				.build();

		Product product = Product.builder().name("Acer Aspire Desktop").alias("acer_aspire_desktop")
				.shortDescription("Short description for Acer Aspire Desktop")
				.fullDescription("Full description for Acer Aspire Desktop").brand(brand).category(category).price(456)
				.cost(456).enabled(true).inStock(true).createdTime(new Date()).updatedTime(new Date()).build();

		Product savedProduct = this.productRepository.save(product);

		assertThat(savedProduct).isNotNull();
		assertThat(savedProduct.getId()).isGreaterThan(0);
	}

	@Test
	public void testGetAllProducts() {
		List<Product> products = this.productRepository.findAll();
		System.out.println(products);
	}

	@Test
	public void testGetProduct() {
		Product product = this.productRepository.findById(1).orElse(null);
		System.out.println(product);

		assertThat(product).isNotNull();
	}

	@Test
	public void testUpdateProduct() {
		Product product = this.productRepository.findById(1).orElse(null);
		Product updateProduct = product.toBuilder().name("SamSung Galaxy A31").alias("samsung_galaxy_A31").build();
		this.productRepository.save(updateProduct);
		assertThat(updateProduct).isNotNull();
	}

	@Test
	public void testDeleteProduct() {
		this.productRepository.deleteById(3);
		
		Product product = this.productRepository.findById(3).orElse(null);

		assertThat(product).isNull();
	}
	
	@Test
	public void testUpdateProductWithImages() {
		Product product = this.productRepository.findById(2).orElse(null);
		
		product.setMainImage("LogoGearvn.png");
		
		product.addExtraImages("LogoGearvn.png");
		product.addExtraImages("LogoGearvn1.png");
		product.addExtraImages("LogoGearvn2.png");
		
		Product savedProduct = this.productRepository.save(product);
		
		assertThat(product).isNotNull();
		assertThat(savedProduct.getImages().size()).isEqualTo(7);
	}
	
	@Test
	public void testCreateProductDetails() {
		Product product = this.productRepository.findById(19).orElse(null);
		if (product != null) {
			product.addProductDetail("ram", "8gb");
			product.addProductDetail("cpu", "core-i5");
			product.addProductDetail("ssd", "516gb");
			
			Product savedProduct = this.productRepository.save(product);
			assertThat(savedProduct.getId()).isGreaterThan(0);
		}
	}
}

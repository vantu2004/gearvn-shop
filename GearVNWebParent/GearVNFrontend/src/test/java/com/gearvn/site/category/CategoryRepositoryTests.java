package com.gearvn.site.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.gearvn.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTests {

	@Autowired
	private CategoryRepository categoryRepository;

	@Test
	public void testGetAllCategoriesEnabled() {
		List<Category> categories = this.categoryRepository.findAllCategoriesEnabled();
		for (Category category : categories) {
			System.out.println(category.getName() + " - " + category.isEnabled());
		}
	}
	
	@Test
	public void testGetCategoryByAlias() {
		Category category = this.categoryRepository.findByAliasEnabled("cellphone_cables_adapters");
		System.out.println(category);
		
		assertThat(category).isNotNull();
	}
}

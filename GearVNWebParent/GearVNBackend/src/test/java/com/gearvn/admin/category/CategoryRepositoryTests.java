package com.gearvn.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
	public void testCreateRootCategory() {
		Category category1 = Category.builder().name("Computers").alias("Computers").image("LogoGearvn.png").build();
		Category category2 = Category.builder().name("Electronics").alias("Electronics").image("LogoGearvn.png")
				.build();

		this.categoryRepository.saveAll(List.of(category1, category2));
	}

	@Test
	public void testCreateSubCategory() {
		Optional<Category> parent1 = this.categoryRepository.findById(1);
		Optional<Category> parent2 = this.categoryRepository.findById(2);
		Optional<Category> parent5 = this.categoryRepository.findById(5);
		Optional<Category> parent6 = this.categoryRepository.findById(4);
		Optional<Category> parent7 = this.categoryRepository.findById(7);
		if (parent5.isPresent()) {
//			Category desktops = Category.builder().name("Desktops").alias("Desktops").image("LogoGearvn.png")
//					.parent(parent1.get()).build();
//			Category laptops = Category.builder().name("Laptops").alias("Laptops").image("LogoGearvn.png")
//					.parent(parent1.get()).build();
//			Category components = Category.builder().name("Computer Components").alias("Computer Components")
//					.image("LogoGearvn.png").parent(parent1.get()).build();

//			Category cameras = Category.builder().name("Cameras").alias("Cameras").image("LogoGearvn.png")
//					.parent(parent2.get()).build();
//			Category smartPhones = Category.builder().name("Smartphones").alias("Smartphones").image("LogoGearvn.png")
//					.parent(parent2.get()).build();

			Category iphones = Category.builder().name("Iphones").alias("Iphones").image("LogoGearvn.png")
					.parent(parent7.get()).build();

			this.categoryRepository.saveAll(List.of(iphones));
		}
	}

	@Test
	public void testGetCategory() {
		Optional<Category> category = this.categoryRepository.findById(1);
		if (category.isPresent()) {
			Category cat = category.get();
			System.out.println("Category Name: " + cat.getName());

			// Lấy danh sách sub-categories
			for (Category sub : cat.getChildren()) {
				System.out.println("Sub-category: " + sub);
			}
		}
	}

	@Test
	public void testGetHierarchicalCategories() {
		List<Category> categories = this.categoryRepository.findAll();

		for (Category rootCategory : categories) {
			// rootCategory thì ko có parent
			if (rootCategory.getParent() == null) {
				System.out.println(rootCategory.getName());

				// duyệt lần 1 cho tập subCategories của rootCategory
				for (Category subCategory : rootCategory.getChildren()) {
					System.out.println("--" + subCategory.getName()); 
					
					// duyệt lần 2
					printChildren(subCategory, 1);
				}
			}
		}
	}
	
	private void printChildren(Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		
		for (Category subCategory : parent.getChildren()) {
			for (int i = 0; i< newSubLevel; i++) {
				System.out.print("--");
			}
			System.out.println(subCategory.getName()); 
			
			// duyệt lần 2++
			printChildren(subCategory, newSubLevel);
		}
	}
	
//	@Test
//	public void testGetListRootCategories() {
//		System.out.println(this.categoryRepository.findRootCategories());
//	}
	

}

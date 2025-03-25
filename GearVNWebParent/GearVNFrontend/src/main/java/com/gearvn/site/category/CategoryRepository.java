package com.gearvn.site.category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gearvn.common.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

	// lấy danh sách categories đã enabled theo asc
	@Query("SELECT c FROM Category c WHERE c.enabled = true ORDER BY c.name ASC")
	List<Category> findAllCategoriesEnabled();

	@Query("SELECT c FROM Category c WHERE c.enabled = true AND c.alias = ?1")
	Category findByAliasEnabled(String alias);

}

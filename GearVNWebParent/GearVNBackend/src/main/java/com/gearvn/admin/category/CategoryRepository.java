package com.gearvn.admin.category;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gearvn.common.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>{

	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
	public List<Category> findRootCategories(Sort sort);
	
	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
	public Page<Category> findRootCategories_Pageable(Pageable pageable);
	
	public Category findByAlias(String alias);

	public Category findByName(String name);
	
	public Long countById (Integer id);
	
	@Query("SELECT c FROM Category c WHERE c.name LIKE %?1% AND c.parent.id is NULL")
	public Page<Category> findRootCategories_Pageable(String keyword, Pageable pageable);
}

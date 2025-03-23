package com.gearvn.admin.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gearvn.common.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

	Product findByName(String name);

	Product findByAlias(String alias);

	Long countById(Integer id);

	@Query("SELECT p FROM Product p WHERE p.name LIKE %?1%" 
			+ " OR p.shortDescription LIKE %?1%"
			+ " OR p.fullDescription LIKE %?1%" 
			+ " OR p.brand.name LIKE %?1%" 
			+ " OR p.category.name LIKE %?1%")
	Page<Product> findAll(String keyword, Pageable pageable);

	Page<Product> findAll(Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.category.id = ?1 OR p.category.allParentIds LIKE %?2%")
	Page<Product> findAll(Integer categoryId, String categoryIdMatch, Pageable pageable);
	
	@Query("SELECT p FROM Product p WHERE "
			+ " ( p.category.id = ?1"
			+ " OR p.category.allParentIds LIKE %?2%)"
			+ " AND "
			+ " ( p.name LIKE %?3%"
			+ " OR p.shortDescription LIKE %?3%"
			+ "	OR p.fullDescription LIKE %?3%" 
			+ " OR p.brand.name LIKE %?3%"
			+ " OR p.category.name LIKE %?3%) ")
	Page<Product> findAll(Integer categoryId, String categoryIdMatch, String keyword, Pageable pageable);
}

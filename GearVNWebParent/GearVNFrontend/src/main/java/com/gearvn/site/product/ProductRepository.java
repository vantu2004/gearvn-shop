package com.gearvn.site.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gearvn.common.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

	// lấy tất cả sản phẩm đã enabled có categoryId truyền vào (kể cả sản phẩm trong
	// subCategory)
	@Query("SELECT p FROM Product p WHERE p.enabled = true "
			+ "AND (p.category.id = ?1 OR p.category.allParentIds LIKE %?2%) " + "ORDER BY p.name ASC")
	Page<Product> findProductsByCategory(Integer categoryId, String categoryIdMatch, Pageable pageable);

	Product findByAlias(String alias);

	// dùng FULLTEXT để search (vào cài đặt 1 table --> tab indexes --> tạo 1 row mới
	// kiểu FULLTEXT --> apply)
	// vẫn chạy được
	@Query(value = "SELECT * FROM products p WHERE p.enabled = true AND MATCH(name, short_description, full_description) AGAINST (?1)", 
		       nativeQuery = true)
		Page<Product> findProductsEnabledByKeyword(String keyword, Pageable pageable);

}

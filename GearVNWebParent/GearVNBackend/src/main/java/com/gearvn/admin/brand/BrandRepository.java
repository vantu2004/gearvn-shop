package com.gearvn.admin.brand;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gearvn.common.entity.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {

	Long countById(Integer id);

	Optional<Brand> findByName(String name);

	@Query("SELECT b FROM Brand b WHERE b.name LIKE %?1%")
	public Page<Brand> findAll(String keyword, Pageable pageable);
	
	public Page<Brand> findAll(Pageable pageable);
}

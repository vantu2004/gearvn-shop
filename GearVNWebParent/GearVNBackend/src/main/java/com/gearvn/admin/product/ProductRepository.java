package com.gearvn.admin.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gearvn.common.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

}

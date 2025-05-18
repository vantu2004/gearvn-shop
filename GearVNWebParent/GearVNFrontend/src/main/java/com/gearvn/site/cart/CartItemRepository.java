package com.gearvn.site.cart;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.gearvn.common.entity.CartItem;
import com.gearvn.common.entity.Customer;
import com.gearvn.common.entity.product.Product;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
	List<CartItem> findByCustomer(Customer customer);

	CartItem findByCustomerAndProduct(Customer customer, Product product);

	@Modifying
	@Query("UPDATE CartItem c SET c.quantity = ?3 WHERE c.customer.id = ?1 AND c.product.id = ?2")
	void updateQuantity(Integer customerId, Integer productId, int quantity);

	@Modifying
	@Query("DELETE FROM CartItem c WHERE c.customer.id = ?1 AND c.product.id = ?2")
	void deleteCartItem(Integer customerId, Integer productId);
}

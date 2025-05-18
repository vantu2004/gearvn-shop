package com.gearvn.site.cart;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.gearvn.common.entity.CartItem;
import com.gearvn.common.entity.Customer;
import com.gearvn.common.entity.product.Product;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CartItemRepositoryTests {

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private TestEntityManager testEntityManager;

	@Test
	public void testAddProductToCart() {
		int customerId = 1;
		int productId = 2;

		Customer customer = this.testEntityManager.find(Customer.class, customerId);
		Product product = this.testEntityManager.find(Product.class, productId);

		CartItem cartItem = new CartItem();
		cartItem.setCustomer(customer);
		cartItem.setProduct(product);
		cartItem.setQuantity(2);

		CartItem savedCartItem = this.cartItemRepository.save(cartItem);

		assertThat(savedCartItem.getId()).isGreaterThan(0);
	}

	@Test
	public void testAddMoreProductsToCart() {

		Customer customer1 = Customer.builder().id(1).build();
		Customer customer2 = Customer.builder().id(2).build();
		Customer customer3 = Customer.builder().id(3).build();

		Product product1 = Product.builder().id(4).build();
		Product product2 = Product.builder().id(5).build();
		Product product3 = Product.builder().id(6).build();

		CartItem cartItem1 = CartItem.builder().customer(customer1).product(product1).quantity(7).build();
		CartItem cartItem2 = CartItem.builder().customer(customer2).product(product2).quantity(8).build();
		CartItem cartItem3 = CartItem.builder().customer(customer3).product(product3).quantity(9).build();

		List<CartItem> cartItems = this.cartItemRepository.saveAll(List.of(cartItem1, cartItem2, cartItem3));

		assertThat(cartItems.size()).isGreaterThan(0);
	}

	@Test
	public void testGetCartItemsByCustomer() {
		Customer customer1 = Customer.builder().id(1).build();

		List<CartItem> cartItems = this.cartItemRepository.findByCustomer(customer1);
		System.out.println(cartItems);

		assertThat(cartItems.size()).isGreaterThan(0);
	}

	@Test
	public void testGetCartItemsByCustomerAndProduct() {
		Customer customer1 = Customer.builder().id(1).build();
		Product product1 = Product.builder().id(5).build();

		CartItem cartItem = this.cartItemRepository.findByCustomerAndProduct(customer1, product1);

		assertThat(cartItem).isNotNull();
	}

	@Test
	public void testUpdateQuantity() {
		this.cartItemRepository.updateQuantity(1, 2, 10);

		CartItem cartItem = this.cartItemRepository.findById(1).orElse(null);
		assertThat(cartItem).isNotNull();
		assertThat(cartItem.getQuantity()).isEqualTo(10);
	}

	@Test
	public void testDeleteCartItem() {
		this.cartItemRepository.deleteCartItem(1, 2);

		CartItem cartItem = this.cartItemRepository.findById(1).orElse(null);
		assertThat(cartItem).isNull();
	}
}

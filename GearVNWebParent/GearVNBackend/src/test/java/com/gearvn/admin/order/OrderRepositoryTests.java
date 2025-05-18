package com.gearvn.admin.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.gearvn.common.entity.Customer;
import com.gearvn.common.entity.order.Order;
import com.gearvn.common.entity.order.OrderDetail;
import com.gearvn.common.entity.order.OrderStatus;
import com.gearvn.common.entity.order.PaymentMethod;
import com.gearvn.common.entity.product.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class OrderRepositoryTests {

	@Autowired
	private OrderRepository repo;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void testCreateNewOrderWithSingleProduct() {
		Customer customer = entityManager.find(Customer.class, 1);
		Product product = entityManager.find(Product.class, 2);

		Order order = new Order();
		order.setCustomer(customer);
		order.copyAddressFromCustomer();
		order.setOrderTime(new Date());
		order.setShippingCost(10);
		order.setProductCost(product.getCost());
		order.setSubtotal(product.getPrice());
		order.setTax(0);
		order.setTotal(product.getPrice() + 10);
		order.setDeliverDays(1);
		order.setDeliverDate(new Date());
		order.setPaymentMethod(PaymentMethod.CREDIT_CARD);
		order.setStatus(OrderStatus.NEW);

		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setQuantity(1);
		orderDetail.setProductCost(product.getCost());
		orderDetail.setShippingCost(10);
		orderDetail.setUnitPrice(product.getPrice());
		orderDetail.setSubtotal(product.getPrice());
		orderDetail.setProduct(product);
		orderDetail.setOrder(order);

		order.getOrderDetails().add(orderDetail);

		Order savedOrder = repo.save(order);

		assertThat(savedOrder.getId()).isGreaterThan(0);
	}

	@Test
	public void testCreateNewOrderWithMultipleProducts() {
		Customer customer = entityManager.find(Customer.class, 43);
		Product product1 = entityManager.find(Product.class, 20);
		Product product2 = entityManager.find(Product.class, 40);

		Order order = new Order();
		order.setCustomer(customer);
		order.copyAddressFromCustomer();
		order.setOrderTime(new Date());

		OrderDetail orderDetail1 = new OrderDetail();
		orderDetail1.setQuantity(1);
		orderDetail1.setProductCost(product1.getCost());
		orderDetail1.setShippingCost(10);
		orderDetail1.setUnitPrice(product1.getPrice());
		orderDetail1.setSubtotal(product1.getPrice());
		orderDetail1.setProduct(product1);
		orderDetail1.setOrder(order);

		OrderDetail orderDetail2 = new OrderDetail();
		orderDetail2.setQuantity(2);
		orderDetail2.setProductCost(product2.getCost());
		orderDetail2.setShippingCost(20);
		orderDetail2.setUnitPrice(product2.getPrice());
		orderDetail2.setSubtotal(product2.getPrice() * 2);
		orderDetail2.setProduct(product2);
		orderDetail2.setOrder(order);

		order.getOrderDetails().add(orderDetail1);
		order.getOrderDetails().add(orderDetail2);

		order.setShippingCost(30);
		order.setProductCost(product1.getCost() + product2.getCost());
		order.setTax(0);
		float subtotal = product1.getPrice() + product2.getPrice() * 2;
		order.setSubtotal(subtotal);
		order.setTotal(subtotal + 30);

		order.setPaymentMethod(PaymentMethod.CREDIT_CARD);
		order.setStatus(OrderStatus.PACKAGED);
		order.setDeliverDate(new Date());
		order.setDeliverDays(3);

		Order savedOrder = repo.save(order);

		assertThat(savedOrder.getId()).isGreaterThan(0);
	}

	@Test
	public void testListOrders() {
		Iterable<Order> orders = repo.findAll();

		assertThat(orders).hasSizeGreaterThan(0);

		orders.forEach(System.out::println);
	}

	@Test
	public void testUpdateOrder() {
		Integer orderId = 2;
		Order order = repo.findById(orderId).get();

		order.setStatus(OrderStatus.SHIPPING);
		order.setPaymentMethod(PaymentMethod.COD);
		order.setOrderTime(new Date());
		order.setDeliverDays(2);

		Order updatedOrder = repo.save(order);

		assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.SHIPPING);
	}

	@Test
	public void testGetOrder() {
		Integer orderId = 2;
		Order order = repo.findById(orderId).get();

		assertThat(order).isNotNull();

		System.out.println(order);
	}

	@Test
	public void testDeleteOrder() {
		Integer orderId = 1;
		repo.deleteById(orderId);

		Optional<Order> result = repo.findById(orderId);
		assertThat(result).isNotPresent();
	}

//	@Test
//	public void testUpdateOrderTracks() {
//		Integer orderId = 19;
//		Order order = repo.findById(orderId).get();
//		
//		OrderTrack newTrack = new OrderTrack();
//		newTrack.setOrder(order);
//		newTrack.setUpdatedTime(new Date());
//		newTrack.setStatus(OrderStatus.NEW);
//		newTrack.setNotes(OrderStatus.NEW.defaultDescription());
//
//		OrderTrack processingTrack = new OrderTrack();
//		processingTrack.setOrder(order);
//		processingTrack.setUpdatedTime(new Date());
//		processingTrack.setStatus(OrderStatus.PROCESSING);
//		processingTrack.setNotes(OrderStatus.PROCESSING.defaultDescription());
//		
//		List<OrderTrack> orderTracks = order.getOrderTracks();
//		orderTracks.add(newTrack);
//		orderTracks.add(processingTrack);
//		
//		Order updatedOrder = repo.save(order);
//		
//		assertThat(updatedOrder.getOrderTracks()).hasSizeGreaterThan(1);
//	}
//	
//	@Test
//	public void testAddTrackWithStatusNewToOrder() {
//		Integer orderId = 2;
//		Order order = repo.findById(orderId).get();
//		
//		OrderTrack newTrack = new OrderTrack();
//		newTrack.setOrder(order);
//		newTrack.setUpdatedTime(new Date());
//		newTrack.setStatus(OrderStatus.NEW);
//		newTrack.setNotes(OrderStatus.NEW.defaultDescription());
//		
//		List<OrderTrack> orderTracks = order.getOrderTracks();
//		orderTracks.add(newTrack);		
//
//		Order updatedOrder = repo.save(order);
//		
//		assertThat(updatedOrder.getOrderTracks()).hasSizeGreaterThan(1);
//	}
//	
//	@Test
//	public void testFindByOrderTimeBetween() throws ParseException {
//		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
//		Date startTime = dateFormatter.parse("2021-08-01");
//		Date endTime = dateFormatter.parse("2021-08-31");
//		
//		List<Order> listOrders = repo.findByOrderTimeBetween(startTime, endTime);
//		
//		assertThat(listOrders.size()).isGreaterThan(0);
//		
//		for (Order order : listOrders) {
//			System.out.printf("%s | %s | %.2f | %.2f | %.2f \n", 
//					order.getId(), order.getOrderTime(), order.getProductCost(), 
//					order.getSubtotal(), order.getTotal());
//		}
//	}
}

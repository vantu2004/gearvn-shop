package com.gearvn.site.order;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gearvn.common.entity.Address;
import com.gearvn.common.entity.CartItem;
import com.gearvn.common.entity.Customer;
import com.gearvn.common.entity.order.Order;
import com.gearvn.common.entity.order.OrderDetail;
import com.gearvn.common.entity.order.OrderStatus;
import com.gearvn.common.entity.order.PaymentMethod;
import com.gearvn.common.entity.product.Product;
import com.gearvn.site.checkout.CheckoutInfo;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	public Order handleSaveOrder(Customer customer, Address address, List<CartItem> cartItems,
			PaymentMethod paymentMethod, CheckoutInfo checkoutInfo) {

		Order order = new Order();

		order.setCustomer(customer);
		
		if (address == null) {
			order.copyAddressFromCustomer();
		} else {
			order.copyShippingAddress(address);
		}

		order.setOrderTime(new Date());
		order.setShippingCost(checkoutInfo.getShippingCostTotal());
		order.setProductCost(checkoutInfo.getProductCost());
		order.setSubtotal(checkoutInfo.getProductTotal());
		order.setTax(0.0f);
		order.setTotal(checkoutInfo.getPaymentTotal());
		order.setDeliverDays(checkoutInfo.getDeliverDays());
		order.setDeliverDate(checkoutInfo.getDeliverDate());
		order.setPaymentMethod(paymentMethod);

		if (paymentMethod.equals(PaymentMethod.PAYPAL)) {
			order.setStatus(OrderStatus.PAID);
		} else {
			order.setStatus(OrderStatus.NEW);
		}

		Set<OrderDetail> orderDetails = order.getOrderDetails();

		for (CartItem cartItem : cartItems) {
			Product product = cartItem.getProduct();

			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setQuantity(cartItem.getQuantity());
			orderDetail.setProductCost(product.getCost() * cartItem.getQuantity());
			orderDetail.setShippingCost(cartItem.getShippingCost());
			orderDetail.setUnitPrice(product.getDiscountPrice());
			orderDetail.setSubtotal(cartItem.getTotalPrice());
			orderDetail.setProduct(product);
			orderDetail.setOrder(order);

			orderDetails.add(orderDetail);
		}

//		OrderTrack track = new OrderTrack();
//		track.setOrder(order);
//		track.setStatus(OrderStatus.NEW);
//		track.setNotes(OrderStatus.NEW.defaultDescription());
//		track.setUpdatedTime(new Date());
//		
//		order.getOrderTracks().add(track);

		return this.orderRepository.save(order);
	}
}

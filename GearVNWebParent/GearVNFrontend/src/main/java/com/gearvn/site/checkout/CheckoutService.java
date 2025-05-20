package com.gearvn.site.checkout;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gearvn.common.entity.CartItem;
import com.gearvn.common.entity.ShippingRate;
import com.gearvn.common.entity.product.Product;

@Service
public class CheckoutService {

	private static final int DIM_DIVISOR = 139;

	public CheckoutInfo prepareCheckout(List<CartItem> cartItems, ShippingRate shippingRate) {
		CheckoutInfo checkoutInfo = new CheckoutInfo();

		float productCost = calculateProductCost(cartItems);
		
		float productTotal = calculateProductTotal(cartItems);
		float shippingCostTotal = calculateShippingCost(cartItems, shippingRate);
		
		float paymentTotal = productTotal + shippingCostTotal;

		checkoutInfo.setProductCost(productCost);
		
		checkoutInfo.setProductTotal(productTotal);
		checkoutInfo.setShippingCostTotal(shippingCostTotal);
		
		checkoutInfo.setDeliverDays(shippingRate.getDays());
		// ko cần setDeliverDate vì bản thân hàm get biến này chỉ cần deliverDays là đủ
		checkoutInfo.setCodSupported(shippingRate.isCodSupported());
		
		checkoutInfo.setPaymentTotal(paymentTotal);

		return checkoutInfo;
	}

	// tính tổng giá nhập của đơn hàng
	private float calculateProductCost(List<CartItem> cartItems) {
		float productCost = 0.0f;
		for (CartItem cartItem : cartItems) {
			productCost += cartItem.getQuantity() * cartItem.getProduct().getCost();
		}

		return productCost;
	}

	// tính tổng giá bán (chưa tính phí phát sinh)
	private float calculateProductTotal(List<CartItem> cartItems) {
		float productTotal = 0.0f;
		for (CartItem cartItem : cartItems) {
			productTotal += cartItem.getTotalPrice();
		}

		return productTotal;
	}

	/*
	 * tính tổng chi phí vận chuyển cho từng loại hàng với số lượng cụ thể trong
	 * toàn bộ đơn hàng
	 */
	private float calculateShippingCost(List<CartItem> cartItems, ShippingRate shippingRate) {
		float shippingCostTotal = 0.0f;

		for (CartItem cartItem : cartItems) {
			Product product = cartItem.getProduct();

			/*
			 * Tính trọng lượng quy đổi theo thể tích (Dimensional Weight) (dài * rộng *
			 * cao)/139
			 */
			float dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;

			// nếu trọng lượng thực tế > thể tích thì lấy thực tế và ngc lại
			float finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;

			/*
			 * Tính phí vận chuyển: trọng lượng × số lượng × giá vận chuyển/1 đơn vị khối
			 * lượng (vd: vật 10kg và rate = 5 --> shippingCost là 10 * 5 = 50)
			 */
			float shippingCost = finalWeight * cartItem.getQuantity() * shippingRate.getRate();

			// Gán chi phí vào cartItem (để hiển thị hoặc lưu)
			cartItem.setShippingCost(shippingCost);

			shippingCostTotal += shippingCost;
		}

		return shippingCostTotal;
	}

}

package com.gearvn.site.checkout.paypal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayPalOrderResponse {
	/*
	 * tên 2 field này phải match vs tên 2 field trong response trả về sau khi thanh
	 * toán paypal thành công
	 */
	private String id;
	private String status;

	public boolean validate(String orderId) {
		return id.equals(orderId) && status.equals("COMPLETED");
	}
}

package com.gearvn.site.checkout;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutInfo {
	// tổng giá nhập của hóa đơn bán
	private float productCost;
	// tổng giá bán của hóa đơn bán (ch tính phụ phí)
	private float productTotal;
	// tổng ship của từng loại hàng (vs số lượng cụ thể) trong đơn bán
	private float shippingCostTotal;
	// tổng tiền khách cần trả
	private float paymentTotal;
	private int deliverDays;
	private Date deliverDate;
	private boolean codSupported;

	public Date getDeliverDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, deliverDays);
		return calendar.getTime();
	}

	public String getPaymentTotalForPayPal() {
		DecimalFormat formatter = new DecimalFormat("##.##");
		return formatter.format(paymentTotal);
	}
}

package com.gearvn.site.paypal;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.gearvn.site.checkout.paypal.PayPalOrderResponse;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class PayPalApiTests {

	private static final String BASE_URL = "https://api.sandbox.paypal.com";
	private static final String GET_ORDER_API = "/v2/checkout/orders/";
	private static final String CLIENT_ID = "Aa9pMzqWiBq_DNUctfFVpwhlVXOsVNMSudx3KS6OzMXhFaNq5YYscHoEvQMCR6oN6ldKMW78BIUFKz79";
	private static final String CLIENT_SECRET = "ENdb3a4m9SZDxYoMgqbfU6T5glVbXHGBsD28NAJTN6IhWsJNrYadGX5g2D9CktCzRrMstuHU6w0Ny9G0";

	@Test
	public void testGetOrderDetails() {
		String orderId = "9C483226TG0734903";
		String requestURL = BASE_URL + GET_ORDER_API + orderId;

		HttpHeaders headers = new HttpHeaders();
		// Chấp nhận định dạng JSON
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		// Thiết lập ngôn ngữ phản hồi là tiếng Anh
		headers.add("Accept-Language", "en_US");
		// Thêm thông tin Basic Auth (Client ID và Secret)
		headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);

		// Tạo request HTTP không có body, chỉ có header
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

		// Khởi tạo RestTemplate để thực hiện gọi API
		RestTemplate restTemplate = new RestTemplate();

		/*
		 * Gửi request GET đến PayPal API và nhận phản hồi dạng đối tượng
		 * PayPalOrderResponse
		 */
		ResponseEntity<PayPalOrderResponse> response = restTemplate.exchange(requestURL, HttpMethod.GET, request,
				PayPalOrderResponse.class);

		// Lấy dữ liệu từ phần body của response
		PayPalOrderResponse orderResponse = response.getBody();

		System.out.println("Order ID: " + orderResponse.getId());
		System.out.println("Validated: " + orderResponse.validate(orderId));
	}

}

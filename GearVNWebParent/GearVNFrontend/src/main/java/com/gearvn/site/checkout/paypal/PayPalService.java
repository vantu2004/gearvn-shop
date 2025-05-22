package com.gearvn.site.checkout.paypal;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.gearvn.site.setting.PaymentSettingBag;
import com.gearvn.site.setting.SettingService;

@Component
public class PayPalService {

	@Autowired
	private SettingService settingService;

	private static final String GET_ORDER_API = "/v2/checkout/orders/";

	// Xác thực đơn hàng dựa vào orderId
	public boolean validateOrder(String orderId) throws PayPalApiException {
		PayPalOrderResponse orderResponse = getOrderDetails(orderId);
		return orderResponse.validate(orderId);
	}

	// Gửi request đến PayPal để lấy chi tiết đơn hàng
	private PayPalOrderResponse getOrderDetails(String orderId) throws PayPalApiException {
		ResponseEntity<PayPalOrderResponse> response = makeRequest(orderId);
		HttpStatusCode statusCode = response.getStatusCode();

		// Nếu không phải mã phản hồi 200 OK, thì ném ra ngoại lệ
		if (!statusCode.equals(HttpStatus.OK)) {
			throwExceptionForNonOKResponse(statusCode);
		}

		return response.getBody();
	}

	// Tạo request HTTP để gọi PayPal API
	private ResponseEntity<PayPalOrderResponse> makeRequest(String orderId) {
		PaymentSettingBag paymentSettings = this.settingService.getPaymentSettingBag();

		// Tạo URL và thông tin xác thực
		String baseURL = paymentSettings.getPayPalApiBaseUrl();
		String requestURL = baseURL + GET_ORDER_API + orderId;
		String clientId = paymentSettings.getPayPalApiClientId();
		String clientSecret = paymentSettings.getPayPalClientSecret();

		// Tạo header cho request
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Accept-Language", "en_US");
		headers.setBasicAuth(clientId, clientSecret);

		// Tạo request không có body, chỉ có header
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

		// Dùng RestTemplate để gửi request
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.exchange(requestURL, HttpMethod.GET, request, PayPalOrderResponse.class);
	}

	// Xử lý các mã lỗi phản hồi không phải 200 OK từ PayPal
	private void throwExceptionForNonOKResponse(HttpStatusCode statusCode) throws PayPalApiException {
		String message;

		if (statusCode.equals(HttpStatus.NOT_FOUND)) {
			message = "Order ID not found";
		} else if (statusCode.equals(HttpStatus.BAD_REQUEST)) {
			message = "Bad Request to PayPal Checkout API";
		} else if (statusCode.equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
			message = "PayPal server error";
		} else {
			message = "PayPal returned non-OK status code: " + statusCode;
		}

		throw new PayPalApiException(message);
	}

}

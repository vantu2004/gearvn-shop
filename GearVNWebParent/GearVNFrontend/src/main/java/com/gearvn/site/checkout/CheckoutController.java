package com.gearvn.site.checkout;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.gearvn.common.entity.Address;
import com.gearvn.common.entity.CartItem;
import com.gearvn.common.entity.Customer;
import com.gearvn.common.entity.ShippingRate;
import com.gearvn.common.entity.order.Order;
import com.gearvn.common.entity.order.PaymentMethod;
import com.gearvn.site.Utility;
import com.gearvn.site.address.AddressService;
import com.gearvn.site.cart.CartItemService;
import com.gearvn.site.checkout.paypal.PayPalApiException;
import com.gearvn.site.checkout.paypal.PayPalService;
import com.gearvn.site.customer.CustomerService;
import com.gearvn.site.order.OrderService;
import com.gearvn.site.setting.CurrencySettingBag;
import com.gearvn.site.setting.EmailSettingBag;
import com.gearvn.site.setting.PaymentSettingBag;
import com.gearvn.site.setting.SettingService;
import com.gearvn.site.shipping.ShippingRateService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CheckoutController {

	@Autowired
	private CheckoutService checkoutService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private AddressService addressService;

	@Autowired
	private ShippingRateService shippingRateService;

	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private SettingService settingService;

	@Autowired
	private PayPalService payPalService;

	@GetMapping("/checkout")
	public String getCheckoutPage(Model model, HttpServletRequest request) {
		Customer customer = this.getAuthenticatedCustomer(request);
		List<CartItem> cartItems = this.cartItemService.getAllCartItems(customer);

		// xét trường hợp customer chọn/ko chọn địa chỉ mặc định (primaryAddress)
		Address defaultAddress = this.addressService.getDefaultAddress(customer);
		ShippingRate shippingRate = null;
		if (defaultAddress != null) {
			model.addAttribute("fullAddress", defaultAddress.getFullAddress());
			shippingRate = this.shippingRateService.getShippingRateForAddress(defaultAddress);
		} else {
			model.addAttribute("fullAddress", customer.getFullAddress());
			shippingRate = this.shippingRateService.getShippingRateForCustomer(customer);
		}

		/*
		 * cover trường hợp trong page checkout nhưng lại muốn set default address khác
		 * nhưng address này ko hỗ trợ shippingRate
		 */
		if (shippingRate == null) {
			return "redirect:/cart";
		}

		CheckoutInfo checkoutInfo = this.checkoutService.prepareCheckout(cartItems, shippingRate);

		String currencyCode = this.settingService.getCurrencyCodeByCurrencyId();

		PaymentSettingBag paymentSettingBag = this.settingService.getPaymentSettingBag();
		String paypalClientId = paymentSettingBag.getPayPalApiClientId();

		model.addAttribute("customer", customer);
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("checkoutInfo", checkoutInfo);
		model.addAttribute("currencyCode", currencyCode);
		model.addAttribute("paypalClientId", paypalClientId);

		return "checkout/checkout";
	}

	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);

		return this.customerService.getCustomerByEmail(email);
	}

	@PostMapping("/place_order")
	public String handlePlaceOrder(HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
		Customer customer = this.getAuthenticatedCustomer(request);
		Address defaultAddress = this.addressService.getDefaultAddress(customer);
		List<CartItem> cartItems = this.cartItemService.getAllCartItems(customer);

		String pm = request.getParameter("paymentMethod");
		PaymentMethod paymentMethod = PaymentMethod.valueOf(pm);

		ShippingRate shippingRate = null;
		if (defaultAddress != null) {
			shippingRate = this.shippingRateService.getShippingRateForAddress(defaultAddress);
		} else {
			shippingRate = this.shippingRateService.getShippingRateForCustomer(customer);
		}

		CheckoutInfo checkoutInfo = this.checkoutService.prepareCheckout(cartItems, shippingRate);

		Order createdOrder = this.orderService.handleSaveOrder(customer, defaultAddress, cartItems, paymentMethod,
				checkoutInfo);

		// tạo đơn hàng xong thì xóa hết cart
		this.cartItemService.deleteCartItemByCustomer(customer);

		sendOrderConfirmationEmail(createdOrder);

		return "checkout/checkout_success";
	}

	private void sendOrderConfirmationEmail(Order createdOrder)
			throws MessagingException, UnsupportedEncodingException {
		EmailSettingBag emailSettingBag = this.settingService.getEmailSettingBag();

		/*
		 * setup cấu hình để chuẩn bị gửi mail với thông tin lưu trong db với
		 * setting_category MAIL_SERVER
		 */
		JavaMailSenderImpl javaMailSenderImpl = Utility.prepareMailSender(emailSettingBag);
		javaMailSenderImpl.setDefaultEncoding("utf-8");

		String toAddress = createdOrder.getCustomer().getEmail();

		String subject = emailSettingBag.getOrderConfirmationSubject();
		subject = subject.replace("[[orderId]]", String.valueOf(createdOrder.getId()));

		String content = emailSettingBag.getOrderConfirmationContent();
		content = getContent(createdOrder, content);

		MimeMessage message = javaMailSenderImpl.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom(emailSettingBag.getFromAddress(), emailSettingBag.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		helper.setText(content, true);

		javaMailSenderImpl.send(message);
	}

	private String getContent(Order createdOrder, String content) {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss, E, dd MMM, yyyy");
		String orderTime = dateFormat.format(createdOrder.getOrderTime());

		// lấy thông tin setting currency từ db để format tổng tiền phải trả
		CurrencySettingBag currencySettingBag = this.settingService.getCurrencySettingBag();
		String totalAmount = Utility.formatCurrency(createdOrder.getTotal(), currencySettingBag);

		content = content.replace("[[name]]", createdOrder.getCustomer().getFullName());
		content = content.replace("[[orderId]]", String.valueOf(createdOrder.getId()));
		content = content.replace("[[orderTime]]", orderTime);
		content = content.replace("[[shippingAddress]]", createdOrder.getFullAddress());
		content = content.replace("[[total]]", totalAmount);
		content = content.replace("[[paymentMethod]]", createdOrder.getPaymentMethod().toString());
		return content;
	}

	@PostMapping("/process_paypal_order")
	public String processPaypalOrder(HttpServletRequest request, Model model)
			throws UnsupportedEncodingException, MessagingException {
		String orderId = request.getParameter("orderId");

		String message = null;
		
		try {
			if (this.payPalService.validateOrder(orderId)) {
				return handlePlaceOrder(request);
			} else {
				message = "ERROR: Transaction could not be completed because order information is invalid.";
			}
		} catch (PayPalApiException e) {
			message = "ERROR: Transaction failed due to: " + e.getMessage();
		}

		model.addAttribute("message", message);

		return "checkout/checkout_fail";
	}
}

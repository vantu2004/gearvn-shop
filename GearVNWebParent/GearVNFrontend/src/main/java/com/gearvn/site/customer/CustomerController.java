package com.gearvn.site.customer;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gearvn.common.entity.Country;
import com.gearvn.common.entity.Customer;
import com.gearvn.site.Utility;
import com.gearvn.site.setting.EmailSettingBag;
import com.gearvn.site.setting.SettingService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private SettingService settingService;

	@GetMapping("/customers/register")
	public String getRegisterPage(Model model) {
		List<Country> countries = this.customerService.getAllCountries();

		model.addAttribute("countries", countries);
		model.addAttribute("customer", new Customer());

		return "registers/register";
	}

	@PostMapping("/customers/save")
	public String handleSaveBrand(Model model, @Valid Customer customer, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, HttpServletRequest request)
			throws UnsupportedEncodingException, MessagingException {
		// validation
		if (bindingResult.hasErrors()) {
			List<Country> countries = this.customerService.getAllCountries();
			model.addAttribute("countries", countries);

			return "registers/register";
		}

		// thực hiện lưu trước
		this.customerService.handleSaveCustomer(customer);

		sendVerificationEmail(request, customer);

		redirectAttributes.addFlashAttribute("message", "The customer has been saved successfully.");
		return "registers/register_success";
	}

	private void sendVerificationEmail(HttpServletRequest request, @Valid Customer customer)
			throws MessagingException, UnsupportedEncodingException {
		// lấy thông tin setting của MAIL_SERVER, MAIL_TEMPLATE
		EmailSettingBag emailSettingBag = this.settingService.getEmailSettingBag();

		// setup cho MAIL_SERVER
		JavaMailSenderImpl javaMailSenderImpl = Utility.prepareMailSender(emailSettingBag);

		String toAddress = customer.getEmail();
		String subject = emailSettingBag.getCustomerVerifySubject();
		String content = emailSettingBag.getCustomerVerifyContent();

		MimeMessage message = javaMailSenderImpl.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom(emailSettingBag.getFromAddress(), emailSettingBag.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);

		// gán tên customer cho content trong mail
		content = content.replace("[[name]]", customer.getFullName());

		/*
		 * tùy chỉnh đường link, lấy contextPath + url ->
		 * http://localhost:8080/GearVN/verify?code=???
		 */
		String verifyURL = Utility.getSiteUrl(request) + "/customers/verify?code=" + customer.getVerificationCode();

		// gán đường dẫn xác thực
		content = content.replace("[[URL]]", verifyURL);

		helper.setText(content, true);

		javaMailSenderImpl.send(message);

		System.out.println("to Address: " + toAddress);
		System.out.println("Verify URL: " + verifyURL);
	}

	@GetMapping("/customers/verify")
	public String verifyCustomer(@RequestParam("code") String code, Model model) {
		boolean verified = this.customerService.isVerified(code);

		return "registers/" + (verified ? "verify_success" : "verify_fail");
	}
}

package com.gearvn.site.customer;

import java.io.UnsupportedEncodingException;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gearvn.common.entity.Customer;
import com.gearvn.site.Utility;
import com.gearvn.site.setting.EmailSettingBag;
import com.gearvn.site.setting.SettingService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ForgotPasswordController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private SettingService settingService;

	@GetMapping("/forgot_password")
	public String getForgotPasswordPage() {
		return "customer/forgot_password";
	}

	@PostMapping("forgot_password")
	public String handleForgotPassword(HttpServletRequest request, Model model) {
		String email = request.getParameter("email");

		try {
			String token = this.customerService.updateResetPasswordToken(email);
			/*
			 * tùy chỉnh đường link, lấy contextPath + url ->
			 * http://localhost:8080/GearVN/verify?code=???
			 */
			String verifyURL = Utility.getSiteUrl(request) + "/reset_password?token=" + token;

			this.sendEmail(email, verifyURL);

			model.addAttribute("success", "Please check email to change your password.");
		} catch (NoSuchElementException e) {
			// TODO: handle exception
			model.addAttribute("fail", e.getMessage());
		} catch (UnsupportedEncodingException | MessagingException e) {
			model.addAttribute("fail", "Could not send the email.");
		}

		return "customer/forgot_password";
	}

	private void sendEmail(String email, String verifyURL) throws MessagingException, UnsupportedEncodingException {
		// lấy thông tin setting của MAIL_SERVER, MAIL_TEMPLATE
		EmailSettingBag emailSettingBag = this.settingService.getEmailSettingBag();

		// setup cho MAIL_SERVER
		JavaMailSenderImpl javaMailSenderImpl = Utility.prepareMailSender(emailSettingBag);

		String toAddress = email;
		String subject = "Here's the link to reset your password.";
		String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
				+ "<p>Click the link below to change the password.</p>" + "<p><a href=\"" + verifyURL
				+ "\">Change your password.</a></p>" + "<br>"
				+ "<p>Ignore this email if you do remember your password, or you have not made the request.</p>";

		MimeMessage message = javaMailSenderImpl.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom(emailSettingBag.getFromAddress(), emailSettingBag.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		// "true" chỉ định content ở dạng HTML
		helper.setText(content, true);

		javaMailSenderImpl.send(message);
	}

	@GetMapping("/reset_password")
	public String getResetPasswordPage(Model model, @RequestParam("token") String token) {
		Customer customer = this.customerService.getCustomerByResetPasswordToken(token);
		if (customer != null) {
			model.addAttribute("token", token);
		} else {
			return "customer/invalid_token";
		}

		return "customer/reset_password";
	}

	@PostMapping("/reset_password")
	public String handleResetPassword(Model model, @RequestParam("token") String token, @RequestParam("password") String password) {
		try {
			this.customerService.handleResetPassword(token, password);

			return "redirect:/login";
		} catch (Exception e) {
			// TODO: handle exception
			return "invalid_token";
		}
	}
}

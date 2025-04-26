package com.gearvn.site.customer;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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
import com.gearvn.site.security.GearvnCustomerDetails;
import com.gearvn.site.security.oauth.CustomerOAuth2User;
import com.gearvn.site.setting.EmailSettingBag;
import com.gearvn.site.setting.SettingService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
	public String handleSaveCustomer(Model model, @Valid Customer customer, BindingResult bindingResult,
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
	}

	@GetMapping("/customers/verify")
	public String verifyCustomer(@RequestParam("code") String code, Model model) {
		boolean verified = this.customerService.isVerified(code);

		return "registers/" + (verified ? "verify_success" : "verify_fail");
	}

	@GetMapping("/customers/update")
	public String getAccoutnDetails(Model model, HttpServletRequest request) {

		String email = this.getEmailOfAuthenticatedCustomer(request);

		if (!StringUtils.isBlank(email)) {
			Customer customer = this.customerService.getCustomerByEmail(email);
			List<Country> countries = this.customerService.getAllCountries();

			model.addAttribute("customer", customer);
			model.addAttribute("countries", countries);
		}

		return "customer/update_customer";
	}

	// có thể dùng @AuthenticationPrincipal như bên class AccountControler
	private String getEmailOfAuthenticatedCustomer(HttpServletRequest request) {
		String email = "";

		Object principal = request.getUserPrincipal();

		/*
		 * chưa login: AnonymousAuthenticationToken
		 */

		/*
		 * đã login: UsernamePasswordAuthenticationToken, OAuth2AuthenticationToken,
		 * RememberMeAuthenticationToken (tick chọn remember-me, thử nhiều lần nhưng vẫn
		 * ko thấy, nhưng để đảm bào thì vẫn thêm vào)
		 */

		if (principal instanceof UsernamePasswordAuthenticationToken
				|| principal instanceof RememberMeAuthenticationToken) {
			email = request.getUserPrincipal().getName();
		} else if (principal instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) principal;
			CustomerOAuth2User customerOAuth2User = (CustomerOAuth2User) oAuth2AuthenticationToken.getPrincipal();
			email = customerOAuth2User.getEmail();
		}

		return email;
	}

	@PostMapping("/customers/save-update")
	public String handleSaveUpdate(Model model, @Valid Customer customer, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, HttpServletRequest request)
			throws UnsupportedEncodingException, MessagingException {
		List<Country> countries = this.customerService.getAllCountries();
		model.addAttribute("countries", countries);

		// validation
		if (bindingResult.hasErrors()) {
			return "customer/update_customer";
		}

		this.updateFullNameForAuthenticatedCustomer(customer, request);

		this.customerService.handleSaveUpdateCustomer(customer);

		redirectAttributes.addFlashAttribute("message", "The customer has been saved successfully.");
		return "redirect:/customers/update";
	}

	private void updateFullNameForAuthenticatedCustomer(Customer customer, HttpServletRequest request) {
		// luôn lấy đc đối tượng đã đăng nhập bất kể hình thức đăng nhập là gì
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		/*
		 * UsernamePasswordAuthenticationToken/RememberMeAuthenticationToken/
		 * OAuth2AuthenticationToken extends từ AbstractAuthenticationToken mà
		 * AbstractAuthenticationToken lại implements Authentication, Authentication lại
		 * có getPrincipal() trả kiểu Object nên chỉ cần ép về kiểu cụ thể là lấy được
		 * đối tượng đăng nhập. Trong 1 số trường hợp thì nên ép về kiểu Token cụ thể để
		 * có thể dùng 1 số phương thức đặc trưng cho từng loại Token
		 */
		if (authentication instanceof UsernamePasswordAuthenticationToken
				|| authentication instanceof RememberMeAuthenticationToken) {
			GearvnCustomerDetails gearvnCustomerDetails = (GearvnCustomerDetails) authentication.getPrincipal();
			gearvnCustomerDetails.setCustomer(customer);
		} else if (authentication instanceof OAuth2AuthenticationToken) {
			CustomerOAuth2User customerOAuth2User = (CustomerOAuth2User) authentication.getPrincipal();
			customerOAuth2User.setFullName(customer.getFullName());
		}

		// Cập nhật lại securityContext vào session
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
		}
	}

}

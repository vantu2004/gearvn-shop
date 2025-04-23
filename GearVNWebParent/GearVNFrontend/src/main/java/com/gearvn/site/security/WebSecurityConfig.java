package com.gearvn.site.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;

import com.gearvn.site.security.oauth.CustomerOAuth2UserService;
import com.gearvn.site.security.oauth.OAuth2LoginSuccessHandler;

import jakarta.servlet.DispatcherType;

@Configuration
public class WebSecurityConfig {

	@Autowired
	private CustomerOAuth2UserService customerOAuth2UserService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SpringSessionRememberMeServices rememberMeServices() {
		SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
		// optionally customize
		rememberMeServices.setAlwaysRemember(false);

		return rememberMeServices;
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(customerDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
	public UserDetailsService customerDetailsService() {
		return new GearvnCustomerDetailsService();
	}

	/*
	 * class config này đc spring ưu tiên chạy sớm, nếu dùng @Autowired để inject
	 * OAuth2LoginSuccessHandler, trong OAuth2LoginSuccessHandler lại yêu cầu inject
	 * CustomerService (spring chưa quản lý vì phải chạy class config trc) --> lỗi.
	 * Việc tạo bean thủ công như này nghĩa là đang tạo mới 1 class
	 * OAuth2LoginSuccessHandler (ko phải inject, nếu inject thì phải tạo qua
	 * constructor và truyền customerService), trong OAuth2LoginSuccessHandler lúc
	 * này mới inject CustomerService (ko lỗi vì các bean đã đc đăng ký quản lý
	 * xong)
	 */
	@Bean
	public AuthenticationSuccessHandler oAuth2LoginSuccessHandler() {
		return new OAuth2LoginSuccessHandler();
	}

	@Bean
	public AuthenticationSuccessHandler databaseLoginSuccessHandler() {
		return new DatabaseLoginSuccessHandler();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// v6. lamda
		http.authorizeHttpRequests(authorize -> authorize
				.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.INCLUDE).permitAll()
				.requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

				// đảm bảo admin/editor có full quyền với product
				// .requestMatchers("/customers/**").authenticated()

				.anyRequest().permitAll())

				.formLogin(formLogin -> formLogin.loginPage("/login").usernameParameter("email").permitAll()
						.successHandler(this.databaseLoginSuccessHandler()).failureUrl("/login?error=true"))

				.oauth2Login(oauth2 -> oauth2.loginPage("/login")
						.userInfoEndpoint(userInfo -> userInfo.userService(this.customerOAuth2UserService))
						.successHandler(this.oAuth2LoginSuccessHandler()).failureUrl("/login?error"))

				.sessionManagement((sessionManagement) -> sessionManagement
						// luôn tạo session cho mọi request
						.sessionCreationPolicy(SessionCreationPolicy.ALWAYS).invalidSessionUrl("/logout?expired")
						/*
						 * tối đa 1 thiết bị đăng nhập trong 1 tài khoản, kết hợp đoạn dưới để cho biết
						 * có nên đá tài khoản còn lại ra không
						 */
						.maximumSessions(1)
						// thiết bị khác đăng nhập đá thiết bị đã đăng nhập
						.maxSessionsPreventsLogin(false))

				// xóa cookie và hủy session
				.logout(logout -> logout.deleteCookies("JSESSIONID").invalidateHttpSession(true))

				// bật rememberMe
				.rememberMe(r -> r.rememberMeServices(rememberMeServices()));

		return http.build();
	}
}

package com.gearvn.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;

import com.gearvn.admin.user.service.UserService;

import jakarta.servlet.DispatcherType;

@Configuration
public class WebSecurityConfig {

	@Bean
	public UserDetailsService userDetailsService() {
		return new GearvnUserDetailsService();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	/*
	 * dùng spring session quản lý session, cách của namHaMinh là tự set
	 * cookie-based Remember Me của Spring Security (cách này ko tốt)
	 */
	@Bean
	public SpringSessionRememberMeServices rememberMeServices() {
		SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
		// optionally customize
		rememberMeServices.setAlwaysRemember(false);

		return rememberMeServices;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, UserService userService) throws Exception {
		// v6. lamda
		http.authorizeHttpRequests(authorize -> authorize
				.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.INCLUDE).permitAll()
				.requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
				/*
				 * sau khi tải listRoles từ db bên GearvnUserDetails, lệnh dưới dùng
				 * hasAuthority (ko tự động thêm ROLE_) dò chính xác trong listRoles vì trong db
				 * cx ko có ROLE_, nếu db có lưu ROLE_ thì dùng hasAuthority()/hasRole() (tự
				 * thêm ROLE_)
				 */
				.requestMatchers("/users/**", "/settings/**", "/countries/**", "/states/**").hasAuthority("Admin")
				.requestMatchers("/categories/**", "/brands/**").hasAnyAuthority("Admin", "Editor")

				/*
				 * admin/editor có full quyền, salesperson có quyền update/save/checkunique/xem
				 * danh sách/xem chi tiết/pagination, shipper chỉ có quyền xem danh sách/xem chi
				 * tiết/pagination
				 */

				// cấp quyền tạo/xóa product
				.requestMatchers("/products/create", "/products/delete/**").hasAnyAuthority("Admin", "Editor")

				/*
				 * cấp quyền update/save(sau khi update)/checkUnique(check unique name/alias khi
				 * update)
				 */
				.requestMatchers("/products/update/**", "/products/save*", "/products/check_duplicate")
				.hasAnyAuthority("Admin", "Editor", "Salesperson")

				// cấp quyền xem danh sách products, pagination, chi tiết product
				.requestMatchers("/products", "/products/page/**", "/products/detail/**")
				.hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper")

				// đảm bảo admin/editor có full quyền với product
				.requestMatchers("/products/**").hasAnyAuthority("Admin", "Editor")

				.anyRequest().authenticated())

				.formLogin(formLogin -> formLogin.loginPage("/login").usernameParameter("email").permitAll()
				.defaultSuccessUrl("/", true).failureUrl("/login?error=true"))

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

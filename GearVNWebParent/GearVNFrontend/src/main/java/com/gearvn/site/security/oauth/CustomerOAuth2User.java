package com.gearvn.site.security.oauth;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

// serialize phục vụ lưu trong session
public class CustomerOAuth2User implements OAuth2User, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OAuth2User oAuth2User;
	private String clientName;
	private String fullName;

	public CustomerOAuth2User(OAuth2User oAuth2User, String clientName) {
		this.oAuth2User = oAuth2User;
		this.clientName = clientName;
	}

	@Override
	public Map<String, Object> getAttributes() {
		// TODO Auto-generated method stub
		return this.oAuth2User.getAttributes();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return this.oAuth2User.getAuthorities();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.fullName != null ? this.fullName : this.oAuth2User.getAttribute("name");
	}

	// dùng khi login gg, thêm 1 hàm bên GearvnCustomerDetails để khi login thường
	public String getFullName() {
		return this.getName();
	}

	public String getEmail() {
		return this.oAuth2User.getAttribute("email");
	}

	public String getClientName() {
		return clientName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}

package com.gearvn.admin.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.gearvn.common.entity.Role;
import com.gearvn.common.entity.User;

public class GearvnUserDetails implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private User user;
	
	public GearvnUserDetails(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<Role> roles = user.getRoles();
		
		// SimpleGrantedAuthority là class kế thừa GrantedAuthority đại diện cho 1 role của 1 user
		// 1 user có thể có nhiều role nên dùng list để lấy hết role
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		
		for (Role role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		}
		
		return authorities;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return user.getEmail();
	}

	@Override
	public boolean isEnabled() {
		return user.isEnabled();
	}
	
	public String getFullName() {
		return this.user.getFirstName() + " " + this.user.getLastName();
	}
	
	public String getEmail() {
		return this.user.getEmail();
	}
	
	public String getPhotos() {
		return this.user.getPhotosImagePath();
	}

	public void setFirstName(String firstName) {
		// TODO Auto-generated method stub
		this.user.setFirstName(firstName);
	}

	public void setLastName(String lastName) {
		// TODO Auto-generated method stub
		this.user.setLastName(lastName);
	}

	public void setPhotos(String photos) {
		// TODO Auto-generated method stub
		this.user.setPhotos(photos);
	}
}

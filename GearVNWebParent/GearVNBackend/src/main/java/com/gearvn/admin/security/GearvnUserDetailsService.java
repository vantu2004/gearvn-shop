package com.gearvn.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.gearvn.admin.user.UserRepository;
import com.gearvn.common.entity.User;

public class GearvnUserDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = this.userRepository.findUserByEmail(email); 
		if (user != null) {
			return new GearvnUserDetails(user);
		}
		
		throw new UsernameNotFoundException("Could not find user with email " + email);
	}

}

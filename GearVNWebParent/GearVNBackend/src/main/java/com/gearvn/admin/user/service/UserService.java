package com.gearvn.admin.user.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gearvn.admin.paging.PagingAndSortingHelper;
import com.gearvn.admin.user.repository.RoleRepository;
import com.gearvn.admin.user.repository.UserRepository;
import com.gearvn.common.entity.Role;
import com.gearvn.common.entity.User;

import jakarta.validation.Valid;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static int USER_PER_PAGE = 4;

	public List<User> getAllUsers() {
		return this.userRepository.findAll(Sort.by("firstName").ascending());
	}

	public void pagination_getAllUsers(int pageNumber, PagingAndSortingHelper helper) {
		helper.listEntitites(pageNumber, USER_PER_PAGE, userRepository);
	}

	public List<Role> getAllRoles() {
		return this.roleRepository.findAll();
	}

	public void handleSaveUser(User user) {
		encodePassword(user);
		this.userRepository.save(user);
	}

	private void encodePassword(User user) {
		String encodedPassword = this.passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}

	public boolean isEmailUnique(String email) {
		User user = this.userRepository.findUserByEmail(email);

		return user == null;
	}

	public User getUserById(Integer id) throws Exception {
		Optional<User> user = this.userRepository.findById(id);
		if (user.isPresent()) {
			return user.get();
		}
		throw new NoSuchElementException("Could not find any user with id " + id);
	}

	public void handleUpdateUser(User oldUser, @Valid User user) {
		if (!oldUser.getPassword().equals(user.getPassword())) {
			encodePassword(user);
		}
		this.userRepository.save(user);
	}

	public void deleteUser(Integer id) {
		Long count = this.userRepository.countById(id);
		if (count == 0 || count == null) {
			throw new NoSuchElementException("Could not find any user with id " + id);
		}

		this.userRepository.deleteById(id);
	}

	public User getUserByEmail(String email) {
		// TODO Auto-generated method stub
		return this.userRepository.findUserByEmail(email);
	}
}

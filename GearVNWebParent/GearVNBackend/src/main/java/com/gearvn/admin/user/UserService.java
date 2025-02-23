package com.gearvn.admin.user;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

	public Page<User> pagination_getAllUsers(int pageNumber, String sortField, String sortType, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortType.equals("asc") ? sort.ascending() : sort.descending();
		
		// pageNumber truyền vào ko bắt đầu từ 0 nên phải -1
		Pageable pageable = PageRequest.of(pageNumber - 1, USER_PER_PAGE, sort);
		
		if (keyword != null) {
			return this.userRepository.findAll(keyword, pageable);
		}
		
		return this.userRepository.findAll(pageable);
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
		if (count == null || count == 0) {
			throw new NoSuchElementException("Could not find any user with id " + id);
		}

		this.userRepository.deleteById(id);
	}

	public User getUserByEmail(String email) {
		// TODO Auto-generated method stub
		return this.userRepository.findUserByEmail(email);
	}
}

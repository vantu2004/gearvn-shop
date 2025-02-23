package com.gearvn.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.gearvn.common.entity.Role;
import com.gearvn.common.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
	@Autowired
	private UserRepository userRepository;

	// giúp thực hiện các thao tác với database mà không cần dùng EntityManager trực
	// tiếp
	@Autowired
	private TestEntityManager testEntityManager;

	// @Test ko dùng với private
	@Test
	public void testCreateUserWithOneRole() {
		User admin = User.builder().email("admin@gmail.com").firstName("admin").lastName("007").password("admin")
				.build();

		// lấy role admin trong db theo id = 1
		Role roleAdmin = testEntityManager.find(Role.class, 1);
		admin.addRole(roleAdmin);

		User savedUser = userRepository.save(admin);

		assertThat(savedUser.getId()).isGreaterThan(0);
	}

	@Test
	public void testCreateUserWithTwoRole() {
		User user = User.builder().email("vantu@gmail.com").firstName("van").lastName("tu").password("vantu").build();

		// lấy role admin trong db theo id = 2
		Role roleEditor = testEntityManager.find(Role.class, 3);
		Role roleAssistant = testEntityManager.find(Role.class, 5);
		user.addRole(roleEditor);
		user.addRole(roleAssistant);

		User savedUser = userRepository.save(user);

		assertThat(savedUser.getId()).isGreaterThan(0);
	}

	@Test
	public void testListAllUsers() {
		List<User> users = userRepository.findAll();
		for (User user : users) {
			System.out.println(user);
		}
	}

	@Test
	public void testGetUserById() {
		Optional<User> user = userRepository.findById(7);
		if (user.isPresent()) {
			System.out.println(user);
		} else {
			throw new RuntimeException("User not found with ID: 1");
		}
	}

	@Test
	public void testUpdateUserDetails() {
		Optional<User> user = userRepository.findById(7);
		if (user.isPresent()) {
			user.get().setEnabled(true);
			System.out.println(user);
		} else {
			throw new RuntimeException("User not found with ID: 1");
		}
	}

	@Test
	public void testUpdateUserRoles() {
		Optional<User> user = userRepository.findById(8);
		if (user.isPresent()) {
			Role roleEditor = testEntityManager.find(Role.class, 3);
			Role roleSalesPerson = testEntityManager.find(Role.class, 2);

			user.get().getRoles().remove(roleEditor);
			user.get().addRole(roleSalesPerson);

			User savedUser = userRepository.save(user.get());
			assertThat(savedUser.getId()).isGreaterThan(0);
			System.out.println(user);
		} else {
			throw new RuntimeException("User not found with ID: 1");
		}
	}

	/*
	 * vì thêm spring.jpa.hibernate.ddl-auto=create trong cấu hình nên mặc định sẽ
	 * tự thêm ON DELETE CASCADE đẫn đến việc khi xóa bên user thì bên role cx bị
	 * xóa theo
	 */
	@Test
	public void testDeleteUser() {
		Integer userId = 8;
		userRepository.deleteById(userId);
	}
	
	@Test
	public void testGetUserByEmail() {
		User user = this.userRepository.findUserByEmail("tulevan526@gmail.com");
		System.out.println(user);
		
		assertThat(user).isNotNull();
	}
	
	@Test
	public void testCountById() {
		Integer userId = 2;
		Long count = this.userRepository.countById(userId);
		
		assertThat(count).isGreaterThan(0);
	}
	
	@Test
	public void testPagination() {
		int pageNumber = 0;
		int pageSize = 5;
		
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("lastName").ascending());		
		Page<User> paginationUsers = this.userRepository.findAll(pageable);
		
		List<User> listUsers = paginationUsers.getContent();
		
		for (User user : listUsers) {
			System.out.print(user);
		}
		
		assertThat(listUsers.size()).isEqualTo(pageSize);
	}
	
	@Test
	public void testFilterUser() {
		int pageNumber = 0;
		int pageSize = 2;
		
		Pageable pageable = PageRequest.of(pageNumber, pageSize);		
		Page<User> paginationUsers = this.userRepository.findAll("bruce", pageable);
		
		List<User> listUsers = paginationUsers.getContent();
		
		for (User user : listUsers) {
			System.out.print(user);
		}
		
		assertThat(listUsers.size()).isEqualTo(pageSize);
	}
}

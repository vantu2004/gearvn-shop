package com.gearvn.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.gearvn.common.entity.Role;

@DataJpaTest
// test với dữ liệu thực, nghĩa là mọi thao tác test đều vs dữ liệu thực
@AutoConfigureTestDatabase(replace = Replace.NONE)
// hủy rollback, nghĩa là mọi thao tác đều tác động thực lên db
@Rollback(false)
public class RoleRepositoryTests {

	@Autowired
	private RoleRepository roleRepository;	
	
	@Test
	public void testCreateFirstRole() {
		Role roleAdmin = new Role("Admin", "manage everything");
		Role savedRole = roleRepository.save(roleAdmin);
		
		// kiểm thử savedRole có đc lưu vào db chưa, nếu chưa thì ném ngoại lệ
		assertThat(savedRole.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateRestRoles() {
		Role roleSalesperson = new Role("Salesperson", "manage product price, "
				+ "customers, shipping, orders and sales report");
		
		Role roleEditor = new Role("Editor", "manage categories, brands, "
				+ "products, articles and menus");
		
		Role roleShipper = new Role("Shipper", "view products, view orders "
				+ "and update order status");
		
		Role roleAssistant = new Role("Assistant", "manage questions and reviews");
		
		roleRepository.saveAll(List.of(roleSalesperson, roleEditor, roleShipper, roleAssistant));
	}
}

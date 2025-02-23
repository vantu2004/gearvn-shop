package com.gearvn.admin.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gearvn.common.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	public User findUserByEmail(String email);

	public Long countById(Integer id);

	public Page<User> findAll(Pageable pageable);

	@Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', u.firstName, ' ', u.lastName, ' ', u.email) LIKE %?1%")
	Page<User> findAll(String keyword, Pageable pageable);

}

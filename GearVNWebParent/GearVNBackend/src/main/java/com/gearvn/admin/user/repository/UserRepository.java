package com.gearvn.admin.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gearvn.admin.paging.SearchPaging;
import com.gearvn.common.entity.User;

@Repository
public interface UserRepository extends SearchPaging<User, Integer> {
	public User findUserByEmail(String email);

	public Long countById(Integer id);

	public Page<User> findAll(Pageable pageable);

	@Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', u.firstName, ' ', u.lastName, ' ', u.email) LIKE %?1%")
	public Page<User> findAll(String keyword, Pageable pageable);

}

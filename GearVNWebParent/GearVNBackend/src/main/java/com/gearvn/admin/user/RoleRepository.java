package com.gearvn.admin.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gearvn.common.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer>{

}

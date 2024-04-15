package com.project.userrole.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.userrole.entity.Role;


public interface RoleRepository extends JpaRepository<Role, Integer> {
	Role findByRole(String role);

}
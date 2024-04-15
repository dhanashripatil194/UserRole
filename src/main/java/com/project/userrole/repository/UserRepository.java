package com.project.userrole.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.userrole.entity.User;


public interface UserRepository extends JpaRepository<User, Integer>{
	User findByUserName(String username);
	boolean existsByUserName(String userName);
}

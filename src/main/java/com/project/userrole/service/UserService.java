package com.project.userrole.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.project.userrole.entity.User;
import com.project.userrole.entity.UserDTO;
import com.project.userrole.repository.UserRepository;


public interface UserService extends UserDetailsService{
	
	boolean existsByUsername(String username);
	
	User save(UserDTO userRegisteredDTO);

	User getUserByUsername(String username);

//	User save(UserDTO userRegisteredDTO, int product_id);

}

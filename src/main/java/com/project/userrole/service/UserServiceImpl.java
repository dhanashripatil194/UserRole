package com.project.userrole.service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.userrole.entity.Product;
import com.project.userrole.entity.Role;
import com.project.userrole.entity.User;
import com.project.userrole.entity.UserDTO;
import com.project.userrole.repository.RoleRepository;
import com.project.userrole.repository.UserRepository;
import com.project.userrole.repository.productRepo;


@Service
public class UserServiceImpl implements UserService{
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	RoleRepository roleRepo;
	
	@Autowired
	productRepo productrepo;
	
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		 User user = userRepo.findByUserName(username);
	     return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), mapRolesToAuthorities(user.getRole()));
	}
	
	public Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles){
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRole())).collect(Collectors.toList());
	}

	@Override
	public User save(UserDTO userRegisteredDTO) {
		Product product = productrepo.findById(userRegisteredDTO.getProductid())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

			
		Role role = new Role();
		if(userRegisteredDTO.getRole().equals("USER"))
		  role = roleRepo.findByRole("ROLE_USER");
		else if(userRegisteredDTO.getRole().equals("ADMIN"))
		 role = roleRepo.findByRole("ROLE_ADMIN");
		User user = new User();
		user.setEmail(userRegisteredDTO.getEmail());
		user.setUserName(userRegisteredDTO.getUserName());
		user.setPassword(passwordEncoder.encode(userRegisteredDTO.getPassword()));
		user.setRole(role);
		user.setAddress(userRegisteredDTO.getAddress());
		user.setAdharNo(userRegisteredDTO.getAdharNo());
		user.setAlternateMobile(userRegisteredDTO.getAlternateMobile());
		user.setCreatedAt(userRegisteredDTO.getCreatedAt());
		user.setDesignation(userRegisteredDTO.getDesignation());
		user.setEmpid(userRegisteredDTO.getEmpid());
		user.setGender(userRegisteredDTO.getGender());
		user.setJoiningDate(userRegisteredDTO.getJoiningDate());
		user.setImage(userRegisteredDTO.getImage());
		user.setMobile(userRegisteredDTO.getMobile());
		user.setPanNo(userRegisteredDTO.getPanNo());
		user.setStatus(userRegisteredDTO.getStatus());
		user.setProduct(product);
		user.setUpdatedAt(userRegisteredDTO.getUpdatedAt());
		return userRepo.save(user);
	}

	@Override
	public User getUserByUsername(String username) {
		 return userRepo.findByUserName(username);
	}
	
	@Override
	
	public boolean existsByUsername(String username) {
		return userRepo.existsByUserName(username);
	}

	}

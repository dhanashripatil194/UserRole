package com.project.userrole.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.userrole.entity.User;
import com.project.userrole.entity.UserDTO;
import com.project.userrole.jwt.JwtTokenUtil;
import com.project.userrole.repository.UserRepository;
import com.project.userrole.service.UserService;


@RestController
public class UserController {

	@Autowired
	UserRepository userRepo;

	@Autowired
	AuthenticationManager authManager;

	@Autowired
	JwtTokenUtil jwtGenVal;
	
	@Autowired
	BCryptPasswordEncoder bcCryptPasswordEncoder;
	
	@Autowired
	UserService userService;


	
	@Value("E://SpringBootWorkspace//H2demo//uploads/")
    private String uploadDir;

    @PostMapping("/registration")
    public ResponseEntity<Object> registerUser(
            @RequestParam("userData") String userDataJson,
            @RequestParam("File") MultipartFile imageFile) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserDTO userDto;
        try {
            userDto = objectMapper.readValue(userDataJson, UserDTO.class);
        } catch (JsonProcessingException e) {
            return generateRespose("Invalid user data JSON format", HttpStatus.BAD_REQUEST, null);
        }

        String username = userDto.getUserName();
        
        if (userService.existsByUsername(username)) {
            return generateRespose("Username already exists", HttpStatus.BAD_REQUEST, userDto);
        }

        try {
            // Normalize the file name
            String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());

            // Save the file to the server's filesystem
            String filePath = uploadDir + File.separator + fileName;
            File destFile = new File(filePath);
            imageFile.transferTo(destFile);

            // Update the imagePath in userDto
            userDto.setImage(filePath);

            // Save user and handle errors
            User users = userService.save(userDto);
            if (users == null) {
                return generateRespose("Not able to save user", HttpStatus.BAD_REQUEST, userDto);
            } else {
                return generateRespose("User saved successfully: " + users.getId(), HttpStatus.OK, users);
            }
        } catch (IOException ex) {
            return generateRespose("Failed to save image file", HttpStatus.INTERNAL_SERVER_ERROR, userDto);
        }
    }
	

	@GetMapping("/genToken")
	public String generateJwtToken(@RequestBody UserDTO userDto) throws Exception {
		
			Authentication authentication = authManager.authenticate(
					new UsernamePasswordAuthenticationToken(userDto.getUserName(), userDto.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		
		return jwtGenVal.generateToken(authentication);
	}

	public ResponseEntity<Object> generateRespose(String message, HttpStatus st, Object responseobj) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("meaasge", message);
		map.put("Status", st.value());
		map.put("data", responseobj);

		return new ResponseEntity<Object>(map, st);
	}
	

	@GetMapping("/userdetails")
	public ResponseEntity<?> getUserDetails(HttpServletRequest request, @RequestParam(required = false) String userName) {
	    String jwtToken = null;

	    // Extract JWT token from Authorization header
	    final String authorizationHeader = request.getHeader("Authorization");
	    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
	        jwtToken = authorizationHeader.substring(7); // Extract JWT token
	    }

	    if (userName != null) {
	        // If JWT token is provided, ensure it matches the  username
	        if (jwtToken != null) {
	            String tokenUsername = jwtGenVal.extractUsername(jwtToken);
	            if (!userName.equals(tokenUsername)) {
	                return ResponseEntity.badRequest().body("Token username does not match provided username");
	            }
	        }
	        // Fetch user details by username
	        User user = userService.getUserByUsername(userName);
	        // Check if user exists
	        if (user != null) {
	            return ResponseEntity.ok(user); // Return user details if found
	        } else {
	            return ResponseEntity.notFound().build(); // Return 404 if user not found
	        }
	    }



	    // If neither username nor JWT token is provided, return bad request
	    return ResponseEntity.badRequest().body("Username not provided");
	}


}
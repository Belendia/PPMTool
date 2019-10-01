package com.belendia.ppmtool.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.belendia.ppmtool.domain.User;
import com.belendia.ppmtool.payload.JWTLoginSuccessResponse;
import com.belendia.ppmtool.payload.LoginRequest;
import com.belendia.ppmtool.security.JwtTokenProvider;
import com.belendia.ppmtool.security.SecurityConstants;
import com.belendia.ppmtool.services.MapValidationErrorService;
import com.belendia.ppmtool.services.UserService;
import com.belendia.ppmtool.validator.UserValidator;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {
	
	@Autowired
	private MapValidationErrorService mapValidationErrorService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserValidator userValidator;
	
	@Autowired
	private JwtTokenProvider tokenProvider;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result) {
		
		ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
		if(errorMap != null) return errorMap;
		
		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = SecurityConstants.TOKEN_PREFIX + tokenProvider.generateToken(authentication);
		
		return ResponseEntity.ok(new JWTLoginSuccessResponse(true, jwt));
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult result) {
		// Validate password match
		userValidator.validate(user, result);
		
		ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
		if(errorMap != null) return errorMap;
		
		User newUser = userService.saveUser(user);
		/*
		 * TODO
		 *  To remove ConfirmPassword from the response @JsonIgnore doesn't work. The best way to do it is using DAO
		 */
		newUser.setConfirmPassword(""); 
		return new ResponseEntity<User>(newUser, HttpStatus.CREATED);
	}
}

package com.nt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nt.entity.Customer;
import com.nt.repository.CustomerRepo;
import com.nt.service.JWTService;

@RestController
@RequestMapping("/api")
public class CustomerRestController {
	@Autowired
	 private PasswordEncoder pwdEncoder;
	@Autowired
	private JWTService jwt;
	@Autowired
	private CustomerRepo repo;
	@Autowired
	private AuthenticationManager authManager;
	@GetMapping("/welcome")
	public String welcome() {
		return "welcome Roshan";
		
	}
	@PostMapping("/register")
	public String registerCuStomer(@RequestBody Customer customer) {
		String encodePwd = pwdEncoder.encode(customer.getPwd());
		repo.save(customer);
		return "Customer Registered";
	}
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody Customer c){
		UsernamePasswordAuthenticationToken token=
				new  UsernamePasswordAuthenticationToken(c.getUname(), c.getPwd());
		try {
			Authentication authenticate = authManager.authenticate(token);
			if(authenticate.isAuthenticated()) {
				String JWTtoken = jwt.generateToken(c.getUname());
				return new ResponseEntity<String>(JWTtoken,HttpStatus.UNAUTHORIZED);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<String>("Bad creadential",HttpStatus.FORBIDDEN);
	}

}

package com.nt.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nt.service.CustomerService;
import com.nt.service.JWTService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class AppFilter extends OncePerRequestFilter {
	@Autowired
	private JWTService jwtService;
	@Autowired
	private CustomerService customerService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token=null;
		String username=null;
		//check authorization header
		String authheader = request.getHeader("Authorization");
		
		//if header present,retrieve bearer token
		if(authheader!=null &&authheader.startsWith("Bearer")) {
			 token = authheader.substring(7);
			 username = jwtService.extractUsername(token);
		}
		//validate the token
		//update security context,if token is valid..
		if(username!=null &&SecurityContextHolder.getContext().getAuthentication()==null){
			UserDetails userDetail = customerService.loadUserByUsername(username);
			if(jwtService.validateToken(token, userDetail)) {
				UsernamePasswordAuthenticationToken authToken=new  UsernamePasswordAuthenticationToken(userDetail,null,userDetail.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
			
		}
		filterChain.doFilter(request, response);
	
		
	}
	

}

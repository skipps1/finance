package com.skipps.finance.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.skipps.finance.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class AuthFilter extends OncePerRequestFilter
{

	private final JwtUtil jwtUtil;

	private final UserService userService;

	public AuthFilter(JwtUtil jwtUtil, UserService userService)
	{
	    this.jwtUtil=jwtUtil;
		this.userService=userService;
	}

	@Override
	protected void doFilterInternal(
	    HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException
	{
        try
        {
            String jwt = parseJwt(request);
            if(jwt != null && jwtUtil.validateJwtToken(jwt)
                && SecurityContextHolder.getContext().getAuthentication() == null)
            {
                String username = jwtUtil.getUsernameFromToken(jwt);
                UserDetails userDetails = userService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        catch (Exception e)
        {
            System.out.println("Cannot set user authentication: " + e);
        }
        filterChain.doFilter(request, response);
	}

	private String parseJwt(HttpServletRequest request)
	{
	    String header = request.getHeader("Authorization");
		if(header != null && header.startsWith("Bearer "))
		{
		    return header.substring(7);
		}
		return null;
	}
}

package com.skipps.finance.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skipps.finance.dto.user.DeleteUserRequest;
import com.skipps.finance.dto.user.UpdateEmailRequest;
import com.skipps.finance.dto.user.UpdatePasswordRequest;
import com.skipps.finance.dto.user.UpdateUsernameRequest;
import com.skipps.finance.dto.user.UserDTO;
import com.skipps.finance.model.UserModel;
import com.skipps.finance.security.JwtUtil;
import com.skipps.finance.service.UserService;

import jakarta.validation.Valid;

@RequestMapping("/api/user")
@RestController
public class UserController {


	private UserService userService;


	private JwtUtil jwtUtil;

	UserController(UserService userService, JwtUtil jwtUtil)
	{
	    this.userService = userService;
		this.jwtUtil = jwtUtil;
	}

	@PutMapping("/username")
	public String updateUsername(@AuthenticationPrincipal UserModel user,
	    @Valid @RequestBody UpdateUsernameRequest request)
	{
        userService.updateUsername(request, user);
        return jwtUtil.generateToken(request.username());
	}

	@PutMapping("/email")
	public ResponseEntity<UserDTO> updateEmail(@AuthenticationPrincipal UserModel user,
	    @Valid @RequestBody UpdateEmailRequest request)
	{
        userService.updateEmail(request, user);

        UserDTO response = new UserDTO(user.getUsername(), request.email());

        return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PutMapping("/password")
	public ResponseEntity<Void> updatePassword(@AuthenticationPrincipal UserModel user,
	    @Valid @RequestBody UpdatePasswordRequest request)
	{
        userService.updatePassword(request, user);
        return ResponseEntity.noContent().build();
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserModel user,
    	@Valid @RequestBody DeleteUserRequest request)
	{
       	userService.deleteUser(request, user);
        return ResponseEntity.noContent().build();
	}
}

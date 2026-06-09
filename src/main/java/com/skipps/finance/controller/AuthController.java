package com.skipps.finance.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skipps.finance.dto.auth.AuthResponse;
import com.skipps.finance.dto.auth.LoginRequest;
import com.skipps.finance.dto.auth.RegisterRequest;
import com.skipps.finance.dto.user.UserDTO;
import com.skipps.finance.model.Role;
import com.skipps.finance.model.UserModel;
import com.skipps.finance.security.JwtUtil;
import com.skipps.finance.service.UserService;

import jakarta.validation.Valid;

import com.skipps.finance.exception.DuplicateResourceException;

@RestController
@RequestMapping("api/auth")
public class AuthController
{

    private final AuthenticationManager authenticationManager;


    private final UserService userService;


    private final PasswordEncoder encoder;


    private final JwtUtil jwtUtil;

    AuthController(
        AuthenticationManager authenticationManager,
        UserService userService,
        PasswordEncoder encoder,
        JwtUtil jwtUtil)
    {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest request)
    {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.username(),
                request.password()));

        UserModel userDetails = (UserModel) authentication.getPrincipal();

        String token = jwtUtil.generateToken(request.username());

        AuthResponse response = new AuthResponse(
            token,
            new UserDTO(request.username(), userDetails.getEmail())
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest request)
    {
        if(userService.existsByUsername(request.username()))
        {
            throw new DuplicateResourceException("Username is already taken");
        }

        if(userService.existsByEmail(request.email()))
        {
            throw new DuplicateResourceException("Email is already taken");
        }

        UserModel newUser = new UserModel(
            request.username(),
            encoder.encode(request.password()),
            request.email());

        newUser.setRole(Role.USER);

        userService.save(newUser);

        String token = jwtUtil.generateToken(request.username());

        AuthResponse response = new AuthResponse(
            token,
            new UserDTO(request.username(), request.email())
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}

package com.skipps.finance.service;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.skipps.finance.dto.user.DeleteUserRequest;
import com.skipps.finance.dto.user.UpdateEmailRequest;
import com.skipps.finance.dto.user.UpdatePasswordRequest;
import com.skipps.finance.dto.user.UpdateUsernameRequest;
import com.skipps.finance.exception.DuplicateResourceException;
import com.skipps.finance.exception.BadRequestException;
import com.skipps.finance.model.UserModel;
import com.skipps.finance.repository.UserRepository;

@Service
public class UserService implements UserDetailsService
{

    private UserRepository userRepository;


    private PasswordEncoder encoder;

    UserService(UserRepository userRepository, PasswordEncoder encoder)
    {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        UserModel user = userRepository.findByUsername(username);

        if(user == null)
        {
            throw new UsernameNotFoundException("User with username: " + username + "was not found");
        }

        return user;
    }

    public boolean existsByUsername(String username)
    {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email)
    {
        return userRepository.existsByEmail(email);
    }

    public void save(UserModel user)
    {
        userRepository.save(user);
    }

    public UserModel updateUsername(UpdateUsernameRequest request, UserModel user)
    {
        if(userRepository.existsByUsername(request.username()) && !user.getUsername().equals(request.username()))
        {
            throw new DuplicateResourceException("Username is already taken");
        }

        user.setUsername(request.username());
        return userRepository.save(user);
    }

    public UserModel updateEmail(UpdateEmailRequest request, UserModel user)
    {
        if(userRepository.existsByEmail(request.email()) && !user.getEmail().equals(request.email()))
        {
            throw new DuplicateResourceException("User with such an email already exists");
        }

        user.setEmail(request.email());
        return userRepository.save(user);
    }

    public UserModel updatePassword(UpdatePasswordRequest request, UserModel user)
    {
        if(!encoder.matches(request.currentPassword(), user.getPassword()))
        {
            throw new BadRequestException("Wrong password");
        }
        user.setPasswordHash(encoder.encode(request.newPassword()));
        return userRepository.save(user);
    }

    public void deleteUser(DeleteUserRequest request, UserModel user)
    {
        if(!encoder.matches(request.password(), user.getPassword()))
        {
            throw new BadRequestException("Wrong password");
        }

        userRepository.delete(user);
    }
}

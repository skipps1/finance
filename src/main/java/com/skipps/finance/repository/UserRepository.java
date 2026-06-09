package com.skipps.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skipps.finance.model.UserModel;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long>
{
    UserModel findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}

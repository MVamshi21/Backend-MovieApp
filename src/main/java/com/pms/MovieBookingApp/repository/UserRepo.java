package com.pms.MovieBookingApp.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pms.MovieBookingApp.model.User;

public interface UserRepo extends MongoRepository<User, String> {
	
	Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
    Optional<User> findByLoginId(String username);

    boolean existsByLoginId(String loginId);
}

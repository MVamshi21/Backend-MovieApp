package com.pms.MovieBookingApp.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pms.MovieBookingApp.model.ERole;
import com.pms.MovieBookingApp.model.Role;

public interface RoleRepo extends MongoRepository<Role, String> {
	
	Optional<Role> findByName(ERole name);

}

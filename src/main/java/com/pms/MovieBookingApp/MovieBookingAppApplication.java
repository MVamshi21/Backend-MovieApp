package com.pms.MovieBookingApp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.pms.MovieBookingApp.model.Movie;
import com.pms.MovieBookingApp.repository.MovieRepo;
import com.pms.MovieBookingApp.repository.RoleRepo;

@SpringBootApplication
public class MovieBookingAppApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(MovieBookingAppApplication.class, args);
	}
	@Autowired
	private MovieRepo movieRepository;
	@Autowired
	private RoleRepo roleRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void run(String... args) throws Exception {
;

		
//		
//
//		Movie movie1 = new Movie("Virupaksha","Miraj",126,"Book ASAP");
//	 	Movie movie2 = new Movie("Dasara","Miraj",122,"Book ASAP");
//	 	Movie movie3 = new Movie("Balagam","Konark",107,"Book ASAP");
		Movie movie3 = new Movie(1,"Balagam","Konark",107,"Book ASAP","https://cdn.pixabay.com/photo/2017/08/30/01/05/milky-way-2695569_1280.jpg");
	 	movieRepository.saveAll(List.of(movie3));
//
//		Role admin = new Role(ERole.ROLE_ADMIN);
//		Role user = new Role(ERole.ROLE_USER);
//
//		roleRepository.saveAll(List.of(admin,user));
	}

}

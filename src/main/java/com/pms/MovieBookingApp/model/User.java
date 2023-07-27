package com.pms.MovieBookingApp.model;

import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(value="user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
	
	@Id
	private ObjectId _id;
	
	@NotBlank
	@Size(max=20)
	private String loginId;
	
	@NotBlank
	private String firstName;
	
	@NotBlank
	private String lastName;
	
	@NotBlank
	@Size(max=50)
	@Email
	private String email;
			
    @NotBlank
    @Size(max = 120)
	private String password;
	
	private Long contactNumber;
	
	@DBRef
    private Set<Role> roles = new HashSet<>();

	public User(@NotBlank @Size(max = 20) String loginId, @NotBlank String firstName, @NotBlank String lastName,
			@NotBlank @Size(max = 50) @Email String email, @NotBlank @Size(max = 120) String password, Long contactNumber,
			Set<Role> roles) {
		super();
		this.loginId = loginId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.contactNumber = contactNumber;
		this.roles = roles;
	}

	
	public User(String loginId, String firstName, String lastName, String email, Long contactNumber, String password) {
        this.loginId = loginId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.password = password;
    }
	
	
	public String getUsername() {
        return this.loginId;
    }

	
	
	


}

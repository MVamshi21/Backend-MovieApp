package com.pms.MovieBookingApp.model;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(value = "ticket")
public class Ticket {
	
	private ObjectId _id;
	private String loginId;
	private String movieName;
	private String theaterName;
	private Integer noOfTickets;
	private List<String> seatNumber;
	
	
	public Ticket(String loginId, String movieName, String theaterName, Integer noOfTickets, List<String> seatNumber) {
		this.loginId = loginId;
		this.movieName = movieName;
		this.theaterName = theaterName;
		this.noOfTickets = noOfTickets;
		this.seatNumber = seatNumber;
	}

}

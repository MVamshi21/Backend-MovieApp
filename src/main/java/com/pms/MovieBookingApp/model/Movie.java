package com.pms.MovieBookingApp.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(value = "movie")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
	
	private ObjectId _id;
    private String movieName;
    private Integer movieid;
    private String theaterName;
    private Integer noOfTicketsAvailable;
    private String ticketsStatus;
    private String img;
	
    public Movie(Integer movieid,String movieName, String theaterName, Integer noOfTicketsAvailable, String ticketsStatus, String img) {
        this.movieid = movieid;
    	this.movieName = movieName;
        this.theaterName = theaterName;
        this.noOfTicketsAvailable = noOfTicketsAvailable;
        this.ticketsStatus = ticketsStatus;
        this.img = img;
    }


    public Movie(String movieName, String theaterName, Integer noOfTicketsAvailable, String img) {
        this.movieName = movieName;
        this.theaterName = theaterName;
        this.noOfTicketsAvailable = noOfTicketsAvailable;
        this.img = img;
    }

    public Movie(ObjectId _id, String movieName, String theaterName, Integer noOfTicketsAvailable) {
        this._id = _id;
        this.movieName = movieName;
        this.theaterName = theaterName;
        this.noOfTicketsAvailable = noOfTicketsAvailable;
    }


	public Movie(Integer movieid) {
		super();
		this.movieid = movieid;
	}

}

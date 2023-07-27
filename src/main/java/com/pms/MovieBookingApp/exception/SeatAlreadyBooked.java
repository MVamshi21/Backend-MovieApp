package com.pms.MovieBookingApp.exception;

public class SeatAlreadyBooked extends RuntimeException {
	public SeatAlreadyBooked(String s) {
		super(s);
	}
}
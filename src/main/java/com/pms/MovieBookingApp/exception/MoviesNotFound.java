package com.pms.MovieBookingApp.exception;

public class MoviesNotFound extends RuntimeException {
	public MoviesNotFound(String noMoviesAreAvailable) {
		super(noMoviesAreAvailable);
	}
}

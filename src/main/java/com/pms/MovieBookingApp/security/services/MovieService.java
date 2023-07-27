package com.pms.MovieBookingApp.security.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pms.MovieBookingApp.model.Movie;
import com.pms.MovieBookingApp.model.Ticket;
import com.pms.MovieBookingApp.repository.MovieRepo;
import com.pms.MovieBookingApp.repository.TicketRepo;

@Service
public class MovieService {

    @Autowired
    private MovieRepo movieRepository;
    @Autowired
    private TicketRepo ticketRepository;

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public List<Movie> getMovieByName(String movieName) {
        return movieRepository.findByMovieName(movieName);
    }

    public List<Ticket> findSeats(String movieName, String theatreName) {
        return ticketRepository.findSeats(movieName,theatreName);
    }

    public List<Movie> findAvailableTickets(String movieName, String theatreName) {
        return movieRepository.findAvailableTickets(movieName,theatreName);
    }

    public void saveTicket(Ticket ticket) {
        ticketRepository.save(ticket);
    }

    public void saveMovie(Movie movie) {
        movieRepository.save(movie);
    }

    public List<Ticket> getAllBookedTickets(String movieName) {
        return ticketRepository.findByMovieName(movieName);
    }

    public List<Movie> findByMovieName(String movieName) {
        return movieRepository.findByMovieName(movieName);
    }

    public void deleteByMovieName(String movieName) {
        movieRepository.deleteByMovieName(movieName);
    }
}

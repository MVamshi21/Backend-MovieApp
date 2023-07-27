package com.pms.MovieBookingApp.controller;



import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.kafka.clients.admin.NewTopic;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pms.MovieBookingApp.exception.MoviesNotFound;
import com.pms.MovieBookingApp.exception.SeatAlreadyBooked;
import com.pms.MovieBookingApp.model.Movie;
import com.pms.MovieBookingApp.model.Ticket;
import com.pms.MovieBookingApp.model.User;
import com.pms.MovieBookingApp.payload.request.LoginRequest;
import com.pms.MovieBookingApp.repository.MovieRepo;
import com.pms.MovieBookingApp.repository.TicketRepo;
import com.pms.MovieBookingApp.repository.UserRepo;
import com.pms.MovieBookingApp.security.services.MovieService;
import com.pms.MovieBookingApp.security.services.UserDetailsServiceImpl;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/moviebooking")
@CrossOrigin("*")
@OpenAPIDefinition(
        info = @Info(
                title = "Movie Application API",
                description = "This API provides endpoints for managing movies."
        )
)
@Slf4j
public class MovieController {
    @Autowired
    private UserRepo userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private MovieService movieService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private NewTopic topic;

    @Autowired
    private TicketRepo ticketRepository;

    @Autowired
    private MovieRepo movieRepository;


    @PutMapping("/{loginId}/forgot")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "reset password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> changePassword(@RequestBody LoginRequest loginRequest, @PathVariable String loginId){
        log.debug("forgot password endopoint accessed by "+loginRequest.getLoginId());
        Optional<User> user1 = userRepository.findByLoginId(loginId);
            User availableUser = user1.get();
            User updatedUser = new User(
                            loginId,
                    availableUser.getFirstName(),
                    availableUser.getLastName(),
                    availableUser.getEmail(),
                    availableUser.getContactNumber(),
                    passwordEncoder.encode(loginRequest.getPassword())
                    );
            updatedUser.set_id(availableUser.get_id());
            updatedUser.setRoles(availableUser.getRoles());
            userRepository.save(updatedUser);
            log.debug(loginRequest.getLoginId()+" has password changed successfully");
            return new ResponseEntity<>("Users password changed successfully",HttpStatus.OK);
    }

    @GetMapping("/all")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "search all movies")
//    @PreAuthorize("hasRole('USER')or hasRole('ADMIN')")
    public ResponseEntity<List<Movie>> getAllMovies(){
        log.debug("here u can access all the available movies");
        List<Movie> movieList = movieService.getAllMovies();
        if(movieList.isEmpty()){
            log.debug("currently no movies are available");
            throw new MoviesNotFound("No Movies are available");
        }
        else{
            log.debug("listed the available movies");
            return new ResponseEntity<>(movieList, HttpStatus.FOUND);
        }
    }

    @GetMapping("/movies/search/{movieName}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "search movies by movie name")
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Movie>> getMovieByName(@PathVariable String movieName){
        log.debug("here search a movie by its name");
        List<Movie> movieList = movieService.getMovieByName(movieName);
        if(movieList.isEmpty()){
            log.debug("currently no movies are available");
            throw new MoviesNotFound("Movies Not Found");
        }
        else
            log.debug("listed the available movies with title:"+movieName);
            return new ResponseEntity<>(movieList,HttpStatus.OK);
    }

    @PostMapping("/{movieName}/booking")@SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "book ticket")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> bookTickets(@RequestBody Ticket ticket, @PathVariable String movieName) {
        log.debug(ticket.getLoginId()+" entered to book tickets");
        List<Ticket> allTickets = movieService.findSeats(movieName,ticket.getTheaterName());
        for(Ticket each : allTickets){
            for(int i = 0; i < ticket.getNoOfTickets(); i++){
                if(each.getSeatNumber().contains(ticket.getSeatNumber().get(i))){
                    log.debug("seat is already booked");
                    throw new SeatAlreadyBooked("Seat number "+ticket.getSeatNumber().get(i)+" is already booked");
                }
            }
        }
        
        
        if(movieService.findAvailableTickets(movieName,ticket.getTheaterName()).get(0).getNoOfTicketsAvailable() >=
                ticket.getNoOfTickets()){

            log.info("available tickets "
                    +movieService.findAvailableTickets(movieName,ticket.getTheaterName()).get(0).getNoOfTicketsAvailable());
            movieService.saveTicket(ticket);
            log.debug(ticket.getLoginId()+" booked "+ticket.getNoOfTickets()+" tickets");
            kafkaTemplate.send(topic.name(),"Movie ticket booked. " +
                    "Booking Details are: "+
            ticket);
          updateAvailableTickectsInMovie(movieName,ticket.getTheaterName(),ticket.getNoOfTickets());
            return new ResponseEntity<>("Tickets Booked Successfully with seat numbers"+ticket.getSeatNumber(),HttpStatus.OK);
        }
        else{
            log.debug("tickets sold out");
            return new ResponseEntity<>("\"All tickets sold out\"",HttpStatus.OK);
        }
    }

    @GetMapping("/getallbookedtickets/{movieName}")@SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "get all booked tickets(Admin Only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Ticket>> getAllBookedTickets(@PathVariable String movieName){
        return new ResponseEntity<>(movieService.getAllBookedTickets(movieName),HttpStatus.OK);
    }

    @PutMapping("/{movieName}/update/{ticketId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateTicketStatus(@PathVariable String movieName, @PathVariable ObjectId ticketId) {
        List<Movie> movie = movieRepository.findByMovieName(movieName);
        
        List<Ticket> ticket = ticketRepository.findBy_id(ticketId);
        if (movie == null) {
            throw new MoviesNotFound("Movie not found: " + movieName);
        }

        if (ticket == null) {
            throw new NoSuchElementException("Ticket Not found:" + ticketId);
        }
        //int ticketsBooked = movieService.getTotalNoTickets(movieName);
        for (Movie movies : movie) {
            if (movies.getNoOfTicketsAvailable() == 0) {
                movies.setTicketsStatus("SOLD OUT");
            } else {
                movies.setTicketsStatus("BOOK ASAP");
            }
            movieService.saveMovie(movies);
        }
        return new ResponseEntity<>("Ticket status updated successfully", HttpStatus.OK);

    }


    @DeleteMapping("/{movieName}/delete")@SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "delete a movie(Admin Only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteMovie(@PathVariable String movieName){
        List<Movie> availableMovies = movieService.findByMovieName(movieName);
        if(availableMovies.isEmpty()){
            throw new MoviesNotFound("No movies Available with moviename "+ movieName);
        }
        else {
            movieService.deleteByMovieName(movieName);
            kafkaTemplate.send(topic.name(),"Movie Deleted by the Admin. "+movieName+" is now not available");
            return new ResponseEntity<>("Movie deleted successfully",HttpStatus.OK);
        }

    }


    private void updateAvailableTickectsInMovie(String moviename,String theatreName,Integer noOfTickets) {
        ObjectId objectId = movieService.findAvailableTickets(moviename,theatreName).get(0).get_id();
        Movie movie = new Movie(
                objectId,
                moviename,
                theatreName,
                movieService.findAvailableTickets(moviename,theatreName).get(0).getNoOfTicketsAvailable() - noOfTickets
        );
        movieService.saveMovie(movie);
    }

}

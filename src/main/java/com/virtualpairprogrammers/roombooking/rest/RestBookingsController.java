package com.virtualpairprogrammers.roombooking.rest;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.virtualpairprogrammers.roombooking.data.BookingRepository;
import com.virtualpairprogrammers.roombooking.model.entities.Booking;

@RestController
@RequestMapping("/api/bookings")
public class RestBookingsController {

	@Autowired
	BookingRepository bookingRepository;
	
	@GetMapping("/{date}")
	public List<Booking> getBookingsByDate(@PathVariable("date") String date) {
		Date sqlDate = Date.valueOf(date);
		return bookingRepository.findAllByDate(sqlDate);
	}
	
	@DeleteMapping("/{id}")
	public void deleteBooking(@PathVariable("id") Long id) {
		bookingRepository.deleteById(id);
	}
	
	@GetMapping() 
	public Booking getBooking(@RequestParam("id") Long id) {
		return bookingRepository.findById(id).get();
	}
	
	@PostMapping()
	public Booking newBooking(@RequestBody Booking booking) {
	    bookingRepository.save(booking);
	    return booking;
	}

	@PutMapping()
	public Booking updateBooking(@RequestBody Booking updatedBooking) {
	    bookingRepository.save(updatedBooking);
	    return updatedBooking;
	}

	
}

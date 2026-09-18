package Hotel_Manager_OOP_Class_.demo.controller;

import Hotel_Manager_OOP_Class_.demo.dto.BookingRequest;
import Hotel_Manager_OOP_Class_.demo.entity.Booking;
import Hotel_Manager_OOP_Class_.demo.entity.Room;
import Hotel_Manager_OOP_Class_.demo.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/available-rooms")
    public ResponseEntity<List<Room>> getAvailableRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(required = false) String roomType) {
        List<Room> rooms = bookingService.findAvailableRooms(checkIn, checkOut, roomType);
        return ResponseEntity.ok(rooms);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        try {
            Booking booking = bookingService.createBooking(request);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        try {
            Booking cancelledBooking = bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok(cancelledBooking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

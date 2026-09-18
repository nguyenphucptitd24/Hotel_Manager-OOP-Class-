package Hotel_Manager_OOP_Class_.demo.controller;

import Hotel_Manager_OOP_Class_.demo.dto.BookingResponseDTO;
import Hotel_Manager_OOP_Class_.demo.dto.CreateBookingRequest;
import Hotel_Manager_OOP_Class_.demo.dto.RoomDTO;
import Hotel_Manager_OOP_Class_.demo.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/available-rooms")
    public ResponseEntity<List<RoomDTO>> getAvailableRooms(
            @RequestParam LocalDateTime checkIn,
            @RequestParam LocalDateTime checkOut,
            @RequestParam(required = false) Integer roomTypeId
    ) {

        List<RoomDTO> rooms = bookingService.getAvailableRooms(
                checkIn,
                checkOut,
                roomTypeId
        );

        return ResponseEntity.ok(rooms);
    }

    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(
            @RequestBody CreateBookingRequest request
    ) {

        BookingResponseDTO response =
                bookingService.createBooking(request);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<String> cancelBooking(
            @PathVariable Integer bookingId,
            @RequestParam(required = false) String reason
    ) {

        bookingService.cancelBooking(bookingId, reason);

        return ResponseEntity.ok(
                "Hủy đơn đặt phòng thành công!"
        );
    }
}

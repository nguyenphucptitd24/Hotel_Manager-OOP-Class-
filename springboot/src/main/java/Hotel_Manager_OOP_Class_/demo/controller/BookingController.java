package com.hotel.controller;

import com.hotel.dto.BookingResponseDTO;
import com.hotel.dto.CreateBookingRequest;
import com.hotel.dto.RoomDTO;
import com.hotel.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // API Công việc 1: Lọc phòng trống
    @GetMapping("/available-rooms")
    public ResponseEntity<List<RoomDTO>> getAvailableRooms(
            @RequestParam("checkIn") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkIn,
            @RequestParam("checkOut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkOut,
            @RequestParam(value = "roomTypeId", required = false) Long roomTypeId) {
        
        List<RoomDTO> rooms = bookingService.getAvailableRooms(checkIn, checkOut, roomTypeId);
        return ResponseEntity.ok(rooms);
    }

    // API Công việc 2: Tạo mới đơn đặt phòng
    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(@RequestBody CreateBookingRequest request) {
        BookingResponseDTO response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

package com.hotel.controller;

import com.hotel.dto.RoomDTO;
import com.hotel.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/available-rooms")
    public ResponseEntity<List<RoomDTO>> getAvailableRooms(
            @RequestParam("checkIn") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkIn,
            @RequestParam("checkOut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkOut,
            @RequestParam(value = "roomTypeId", required = false) Long roomTypeId) {
        
        List<RoomDTO> rooms = bookingService.getAvailableRooms(checkIn, checkOut, roomTypeId);
        return ResponseEntity.ok(rooms);
    }
}

package com.hotel.service;

import com.hotel.dto.BookingResponseDTO;
import com.hotel.dto.CreateBookingRequest;
import com.hotel.dto.RoomDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    // Công việc 1: Lọc phòng trống
    List<RoomDTO> getAvailableRooms(LocalDateTime checkIn, LocalDateTime checkOut, Long roomTypeId);

    // Công việc 2: Tạo đơn đặt phòng
    BookingResponseDTO createBooking(CreateBookingRequest request);

    // Công việc 3: Hủy đơn đặt phòng
    void cancelBooking(Long bookingId, String cancelReason);
}

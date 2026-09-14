package Hotel_Manager_OOP_Class_.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import Hotel_Manager_OOP_Class_.demo.dto.BookingResponseDTO;
import Hotel_Manager_OOP_Class_.demo.dto.CreateBookingRequest;
import Hotel_Manager_OOP_Class_.demo.dto.RoomDTO;

public interface BookingService {
    List<RoomDTO> getAvailableRooms(LocalDateTime checkIn, LocalDateTime checkOut, Integer roomTypeId);

    BookingResponseDTO createBooking(CreateBookingRequest request);

    void cancelBooking(Integer bookingId, String cancelReason);
}

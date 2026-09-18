package Hotel_Manager_OOP_Class_.demo.service;

import Hotel_Manager_OOP_Class_.demo.dto.BookingRequest;
import Hotel_Manager_OOP_Class_.demo.entity.Booking;
import Hotel_Manager_OOP_Class_.demo.entity.Room;
import Hotel_Manager_OOP_Class_.demo.repository.BookingRepository;
import Hotel_Manager_OOP_Class_.demo.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    public List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, String roomType) {
        return roomRepository.findAvailableRooms(checkIn, checkOut, roomType);
    }

    @Transactional
    public Booking createBooking(BookingRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setDepositAmount(request.getDepositAmount());
        booking.setStatus("BOOKED");

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Đơn đặt phòng không tồn tại"));

        booking.setStatus("CANCELLED");
        return bookingRepository.save(booking);
    }
}

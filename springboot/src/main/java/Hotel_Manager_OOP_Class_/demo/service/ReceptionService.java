package Hotel_Manager_OOP_Class.demo.service;

import Hotel_Manager_OOP_Class.demo.dto.CheckInRequestDTO;
import Hotel_Manager_OOP_Class.demo.entity.Booking;
import Hotel_Manager_OOP_Class.demo.entity.Room;
import Hotel_Manager_OOP_Class.demo.repository.BookingRepository;
import Hotel_Manager_OOP_Class.demo.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ReceptionService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Transactional
    public Booking checkIn(CheckInRequestDTO request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt phòng!"));

        Room room = booking.getRoom();
        if (room == null && request.getRoomId() != null) {
            room = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng!"));
            booking.setRoom(room);
        }

        if (room != null) {
            room.setStatus("OCCUPIED");
            roomRepository.save(room);
        }

        booking.setStatus("CHECKED_IN");
        booking.setCheckInDate(LocalDateTime.now());
        if (request.getNote() != null) {
            booking.setNote(request.getNote());
        }

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking checkOut(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin đặt phòng!"));

        Room room = booking.getRoom();
        if (room != null) {
            room.setStatus("CLEANING");
            roomRepository.save(room);
        }

        booking.setStatus("CHECKED_OUT");
        booking.setCheckOutDate(LocalDateTime.now());
        return bookingRepository.save(booking);
    }
}
package com.hotel.service.impl;
package com.hotel.service;


import com.hotel.dto.RoomDTO;
import com.hotel.entity.Room;
import com.hotel.exception.BadRequestException;
import com.hotel.mapper.RoomMapper;
import com.hotel.repository.RoomRepository;
import com.hotel.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import com.hotel.dto.BookingResponseDTO;
import com.hotel.dto.CreateBookingRequest;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    // Inject Repository của Thành viên 1
    private final RoomRepository roomRepository;

    @Override
    public List<RoomDTO> getAvailableRooms(LocalDateTime checkIn, LocalDateTime checkOut, Long roomTypeId) {
        
        // 1. Kiểm tra tính hợp lệ của ngày đặt
        if (checkIn == null || checkOut == null) {
            throw new BadRequestException("Ngày nhận và trả phòng không được để trống!");
        }

        if (checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) {
            throw new BadRequestException("Ngày nhận phòng phải trước ngày trả phòng!");
        }

        // 2. Gọi hàm kiểm tra từ Repository của Thành viên 1
        List<Room> availableRooms = roomRepository.findAvailableRooms(checkIn, checkOut, roomTypeId);

        // 3. Chuyển đổi Entity sang DTO để trả về cho Controller
        return availableRooms.stream()
                .map(RoomMapper::toDto)
                .toList();
    }
}




public interface BookingService {
    // Công việc 1
    List<RoomDTO> getAvailableRooms(LocalDateTime checkIn, LocalDateTime checkOut, Long roomTypeId);

    // Công việc 2
    BookingResponseDTO createBooking(CreateBookingRequest request);
}

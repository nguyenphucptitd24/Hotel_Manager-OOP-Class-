package com.hotel.service.impl;

import com.hotel.dto.*;
import com.hotel.entity.*;
import com.hotel.exception.BadRequestException;
import com.hotel.exception.ResourceNotFoundException;
import com.hotel.mapper.BookingMapper;
import com.hotel.repository.*;
import com.hotel.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final RoomRepository roomRepository; // Của Thành viên 1
    private final CustomerRepository customerRepository; // Của Thành viên 3
    private final BookingRepository bookingRepository; 
    private final BookingDetailRepository bookingDetailRepository;

    @Override
    public List<RoomDTO> getAvailableRooms(LocalDateTime checkIn, LocalDateTime checkOut, Long roomTypeId) {
        // ... (Code của Công việc 1 đã paste trước đó) ...
        return List.of();
    }

    @Override
    @Transactional // Đảm bảo nếu lưu lỗi ở bất kỳ bước nào thì DB sẽ Rollback hoàn toàn
    public BookingResponseDTO createBooking(CreateBookingRequest request) {
        
        // 1. Kiểm tra Khách hàng
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin khách hàng!"));

        // 2. Validate danh sách phòng
        if (request.getRoomIds() == null || request.getRoomIds().isEmpty()) {
            throw new BadRequestException("Phải chọn ít nhất 1 phòng để đặt!");
        }

        // 3. Lấy danh sách các phòng từ CSDL & kiểm tra phòng tồn tại
        List<Room> selectedRooms = roomRepository.findAllById(request.getRoomIds());
        if (selectedRooms.size() != request.getRoomIds().size()) {
            throw new BadRequestException("Một hoặc nhiều phòng chọn không tồn tại trong hệ thống!");
        }

        // 4. Tính toán số đêm ở và tổng tiền phòng
        long numberOfNights = Duration.between(request.getCheckInExpected(), request.getCheckOutExpected()).toDays();
        if (numberOfNights <= 0) {
            numberOfNights = 1; // Tính tối thiểu 1 đêm
        }

        double totalRoomPrice = 0.0;
        for (Room room : selectedRooms) {
            totalRoomPrice += room.getPrice() * numberOfNights;
        }

        // Tính tiền cọc 30%
        double totalDeposit = totalRoomPrice * 0.30;

        // 5. Sinh mã Đặt phòng tự động (Ví dụ: BK-20260907-A1B2)
        String bookingCode = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 6. Lưu vào bảng chính (bookings)
        Booking booking = new Booking();
        booking.setBookingCode(bookingCode);
        booking.setCustomer(customer);
        booking.setCheckInExpected(request.getCheckInExpected());
        booking.setCheckOutExpected(request.getCheckOutExpected());
        booking.setTotalPrice(totalRoomPrice);
        booking.setTotalDeposit(totalDeposit);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setNote(request.getNote());

        Booking savedBooking = bookingRepository.save(booking);

        // 7. Lưu vào bảng chi tiết (booking_details - tương ứng file CSV của dự án)
        List<BookingDetail> details = new ArrayList<>();
        for (Room room : selectedRooms) {
            BookingDetail detail = new BookingDetail();
            detail.setBooking(savedBooking);
            detail.setRoom(room);
            detail.setPrice(room.getPrice()); // Lưu giá phòng tại thời điểm đặt
            details.add(detail);
        }
        bookingDetailRepository.saveAll(details);

        // 8. Trả về thông tin đặt phòng hoàn tất
        return BookingMapper.toResponseDto(savedBooking, details);
    }
}

package com.hotel.service.impl;

import com.hotel.dto.BookingResponseDTO;
import com.hotel.dto.CreateBookingRequest;
import com.hotel.dto.RoomDTO;
import com.hotel.entity.*;
import com.hotel.exception.BadRequestException;
import com.hotel.exception.ResourceNotFoundException;
import com.hotel.mapper.BookingMapper;
import com.hotel.mapper.RoomMapper;
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

    private final RoomRepository roomRepository; 
    private final CustomerRepository customerRepository; 
    private final BookingRepository bookingRepository; 
    private final BookingDetailRepository bookingDetailRepository;

    // --- CÔNG VIỆC 1: Lọc phòng trống ---
    @Override
    public List<RoomDTO> getAvailableRooms(LocalDateTime checkIn, LocalDateTime checkOut, Long roomTypeId) {
        if (checkIn == null || checkOut == null) {
            throw new BadRequestException("Ngày nhận và trả phòng không được để trống!");
        }

        if (checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) {
            throw new BadRequestException("Ngày nhận phòng phải trước ngày trả phòng!");
        }

        List<Room> availableRooms = roomRepository.findAvailableRooms(checkIn, checkOut, roomTypeId);

        return availableRooms.stream()
                .map(RoomMapper::toDto)
                .toList();
    }

    // --- CÔNG VIỆC 2: Tạo đơn đặt phòng ---
    @Override
    @Transactional
    public BookingResponseDTO createBooking(CreateBookingRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin khách hàng!"));

        if (request.getRoomIds() == null || request.getRoomIds().isEmpty()) {
            throw new BadRequestException("Phải chọn ít nhất 1 phòng để đặt!");
        }

        List<Room> selectedRooms = roomRepository.findAllById(request.getRoomIds());
        if (selectedRooms.size() != request.getRoomIds().size()) {
            throw new BadRequestException("Một hoặc nhiều phòng chọn không tồn tại!");
        }

        long numberOfNights = Duration.between(request.getCheckInExpected(), request.getCheckOutExpected()).toDays();
        if (numberOfNights <= 0) {
            numberOfNights = 1;
        }

        double totalRoomPrice = 0.0;
        for (Room room : selectedRooms) {
            totalRoomPrice += room.getPrice() * numberOfNights;
        }

        double totalDeposit = totalRoomPrice * 0.30;
        String bookingCode = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Booking booking = new Booking();
        booking.setBookingCode(bookingCode);
        booking.setCustomer(customer);
        booking.setCheckInExpected(request.getCheckInExpected());
        booking.setCheckOutExpected(request.getCheckOutExpected());
        booking.setTotalPrice(totalRoomPrice);
        booking.setTotalDeposit(totalDeposit);
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking savedBooking = bookingRepository.save(booking);

        List<BookingDetail> details = new ArrayList<>();
        for (Room room : selectedRooms) {
            BookingDetail detail = new BookingDetail();
            detail.setBooking(savedBooking);
            detail.setRoom(room);
            detail.setPrice(room.getPrice());
            details.add(detail);
        }
        bookingDetailRepository.saveAll(details);

        return BookingMapper.toResponseDto(savedBooking, details);
    }

    // --- CÔNG VIỆC 3: Hủy đơn đặt phòng ---
    @Override
    @Transactional
    public void cancelBooking(Long bookingId, String cancelReason) {
        // 1. Kiểm tra đơn đặt phòng có tồn tại không
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn đặt phòng có ID: " + bookingId));

        // 2. Kiểm tra điều kiện trạng thái (Không cho hủy nếu đã Check-in hoặc Hoàn thành)
        if (booking.getStatus() == BookingStatus.CHECKED_IN || booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("Không thể hủy đơn đặt phòng đã nhận phòng hoặc đã hoàn thành!");
        }

        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new BadRequestException("Đơn đặt phòng này đã bị hủy trước đó!");
        }

        // 3. Cập nhật trạng thái CANCELED và lý do hủy
        booking.setStatus(BookingStatus.CANCELED);
        booking.setNote(cancelReason != null ? "Lý do hủy: " + cancelReason : "Khách hàng yêu cầu hủy");

        bookingRepository.save(booking);
    }
}

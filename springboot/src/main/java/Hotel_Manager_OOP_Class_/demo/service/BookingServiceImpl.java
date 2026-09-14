package Hotel_Manager_OOP_Class_.demo.service;

import Hotel_Manager_OOP_Class_.demo.dto.BookingResponseDTO;
import Hotel_Manager_OOP_Class_.demo.dto.CreateBookingRequest;
import Hotel_Manager_OOP_Class_.demo.dto.RoomDTO;
import Hotel_Manager_OOP_Class_.demo.entity.Booking;
import Hotel_Manager_OOP_Class_.demo.entity.BookingDetail;
import Hotel_Manager_OOP_Class_.demo.entity.Customer;
import Hotel_Manager_OOP_Class_.demo.entity.Room;
import Hotel_Manager_OOP_Class_.demo.repository.BookingDetailRepository;
import Hotel_Manager_OOP_Class_.demo.repository.BookingRepository;
import Hotel_Manager_OOP_Class_.demo.repository.CustomerRepository;
import Hotel_Manager_OOP_Class_.demo.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Override
    public List<RoomDTO> getAvailableRooms(LocalDateTime checkIn, LocalDateTime checkOut, Integer roomTypeId) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Ngày nhận và trả phòng không được để trống!");
        }

        if (!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("Ngày nhận phòng phải trước ngày trả phòng!");
        }

        List<Room> availableRooms = roomRepository.findAvailableRooms(checkIn.toString(), checkOut.toString());
        if (roomTypeId != null) {
            availableRooms = availableRooms.stream()
                    .filter(room -> room.getRoomType() != null && room.getRoomType().getId().equals(roomTypeId))
                    .toList();
        }

        return availableRooms.stream()
                .map(this::toRoomDto)
                .toList();
    }

    @Override
    @Transactional
    public BookingResponseDTO createBooking(CreateBookingRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Yêu cầu đặt phòng không hợp lệ!");
        }

        if (request.getCustomerId() == null) {
            throw new IllegalArgumentException("Khách hàng không được để trống!");
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin khách hàng!"));

        if (request.getRoomIds() == null || request.getRoomIds().isEmpty()) {
            throw new IllegalArgumentException("Phải chọn ít nhất 1 phòng để đặt!");
        }

        if (request.getCheckInExpected() == null || request.getCheckOutExpected() == null) {
            throw new IllegalArgumentException("Ngày nhận và trả phòng không được để trống!");
        }

        List<Room> selectedRooms = roomRepository.findAllById(request.getRoomIds());
        if (selectedRooms.size() != request.getRoomIds().size()) {
            throw new IllegalArgumentException("Một hoặc nhiều phòng chọn không tồn tại!");
        }

        long numberOfNights = Duration.between(request.getCheckInExpected(), request.getCheckOutExpected()).toDays();
        if (numberOfNights <= 0) {
            numberOfNights = 1;
        }

        BigDecimal totalRoomPrice = BigDecimal.ZERO;
        for (Room room : selectedRooms) {
            BigDecimal roomPrice = room.getRoomType().getBasePrice();
            totalRoomPrice = totalRoomPrice.add(roomPrice.multiply(BigDecimal.valueOf(numberOfNights)));
        }

        BigDecimal totalDeposit = totalRoomPrice.multiply(BigDecimal.valueOf(0.30));
        String bookingCode = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Booking booking = new Booking();
        booking.setBookingCode(bookingCode);
        booking.setCustomer(customer);
        booking.setBookingDate(LocalDateTime.now().toString());
        booking.setTotalDeposit(totalDeposit);
        booking.setStatus("CONFIRMED");

        Booking savedBooking = bookingRepository.save(booking);

        List<BookingDetail> details = new ArrayList<>();
        for (Room room : selectedRooms) {
            BookingDetail detail = new BookingDetail();
            detail.setBooking(savedBooking);
            detail.setRoom(room);
            detail.setCheckInExpected(request.getCheckInExpected().toString());
            detail.setCheckOutExpected(request.getCheckOutExpected().toString());
            detail.setPricePerNight(room.getRoomType().getBasePrice());
            details.add(detail);
        }
        bookingDetailRepository.saveAll(details);

        return new BookingResponseDTO(
                savedBooking.getId(),
                savedBooking.getBookingCode(),
                customer.getId(),
                request.getCheckInExpected(),
                request.getCheckOutExpected(),
                totalRoomPrice,
                totalDeposit,
                savedBooking.getStatus(),
                selectedRooms.stream().map(Room::getId).toList()
        );
    }

    @Override
    @Transactional
    public void cancelBooking(Integer bookingId, String cancelReason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt phòng có ID: " + bookingId));

        if ("CHECKED_IN".equals(booking.getStatus()) || "COMPLETED".equals(booking.getStatus())) {
            throw new IllegalArgumentException("Không thể hủy đơn đặt phòng đã nhận phòng hoặc đã hoàn thành!");
        }

        if ("CANCELED".equals(booking.getStatus())) {
            throw new IllegalArgumentException("Đơn đặt phòng này đã bị hủy trước đó!");
        }

        booking.setStatus("CANCELED");
        bookingRepository.save(booking);
    }

    private RoomDTO toRoomDto(Room room) {
        return new RoomDTO(
                room.getId(),
                room.getRoomNumber(),
                room.getFloor(),
                room.getStatus(),
                room.getRoomType() != null ? room.getRoomType().getId() : null,
                room.getRoomType() != null ? room.getRoomType().getName() : null,
                room.getRoomType() != null ? room.getRoomType().getBasePrice() : null
        );
    }
}

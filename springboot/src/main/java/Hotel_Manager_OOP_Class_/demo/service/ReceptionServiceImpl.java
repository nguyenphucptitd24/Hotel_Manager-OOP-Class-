package Hotel_Manager_OOP_Class_.demo.service;

import Hotel_Manager_OOP_Class_.demo.dto.CheckInRequestDTO;
import Hotel_Manager_OOP_Class_.demo.dto.ReceptionBookingDTO;
import Hotel_Manager_OOP_Class_.demo.dto.ReceptionResponseDTO;
import Hotel_Manager_OOP_Class_.demo.entity.Booking;
import Hotel_Manager_OOP_Class_.demo.entity.BookingDetail;
import Hotel_Manager_OOP_Class_.demo.entity.Room;
import Hotel_Manager_OOP_Class_.demo.repository.BookingDetailRepository;
import Hotel_Manager_OOP_Class_.demo.repository.BookingRepository;
import Hotel_Manager_OOP_Class_.demo.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReceptionServiceImpl implements ReceptionService {

    private final BookingRepository bookingRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final RoomRepository roomRepository;

    @Override
    @Transactional
    public ReceptionResponseDTO checkIn(CheckInRequestDTO request) {
        if (request == null || request.getBookingId() == null) {
            throw new IllegalArgumentException("Mã đơn đặt phòng (bookingId) không được để trống!");
        }

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt phòng có ID: " + request.getBookingId()));

        if ("CANCELLED".equalsIgnoreCase(booking.getStatus()) || "CANCELED".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException("Đơn đặt phòng đã bị hủy, không thể thực hiện check-in!");
        }

        List<BookingDetail> details = bookingDetailRepository.findByBookingId(booking.getId());
        if (details.isEmpty()) {
            throw new RuntimeException("Đơn đặt phòng chưa có chi tiết phòng nào!");
        }

        String now = LocalDateTime.now().toString();
        String roomNumberUpdated = "";

        if (request.getRoomId() != null) {
            Room room = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng có ID: " + request.getRoomId()));

            room.setStatus("OCCUPIED");
            roomRepository.save(room);
            roomNumberUpdated = room.getRoomNumber();

            for (BookingDetail detail : details) {
                if (detail.getRoom() != null && detail.getRoom().getId().equals(request.getRoomId())) {
                    detail.setCheckInActual(now);
                    bookingDetailRepository.save(detail);
                }
            }
        } else {
            List<String> roomNums = new ArrayList<>();
            for (BookingDetail detail : details) {
                Room room = detail.getRoom();
                if (room != null) {
                    room.setStatus("OCCUPIED");
                    roomRepository.save(room);
                    roomNums.add(room.getRoomNumber());
                }
                detail.setCheckInActual(now);
                bookingDetailRepository.save(detail);
            }
            roomNumberUpdated = String.join(", ", roomNums);
        }

        booking.setStatus("CHECKED_IN");
        bookingRepository.save(booking);

        return ReceptionResponseDTO.builder()
                .message("Check-in thành công cho đơn " + booking.getBookingCode() + "!")
                .bookingId(booking.getId())
                .roomId(request.getRoomId())
                .roomNumber(roomNumberUpdated)
                .status("CHECKED_IN")
                .build();
    }

    @Override
    @Transactional
    public ReceptionResponseDTO checkInById(Integer bookingId) {
        return checkIn(new CheckInRequestDTO(bookingId, null, null));
    }

    @Override
    @Transactional
    public ReceptionResponseDTO checkOut(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID không được để trống!");
        }

        // Trường hợp 1: id là bookingId
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            List<BookingDetail> details = bookingDetailRepository.findByBookingId(booking.getId());
            String now = LocalDateTime.now().toString();
            List<String> roomNums = new ArrayList<>();

            for (BookingDetail detail : details) {
                if (detail.getCheckOutActual() == null) {
                    detail.setCheckOutActual(now);
                    bookingDetailRepository.save(detail);
                }
                Room room = detail.getRoom();
                if (room != null) {
                    room.setStatus("CLEANING");
                    roomRepository.save(room);
                    roomNums.add(room.getRoomNumber());
                }
            }

            booking.setStatus("COMPLETED");
            bookingRepository.save(booking);

            return ReceptionResponseDTO.builder()
                    .message("Check-out thành công cho đơn " + booking.getBookingCode() + "! Phòng chuyển sang dọn dẹp.")
                    .bookingId(booking.getId())
                    .roomNumber(String.join(", ", roomNums))
                    .status("CLEANING")
                    .build();
        }

        // Trường hợp 2: id là roomId
        Optional<Room> roomOpt = roomRepository.findById(id);
        if (roomOpt.isPresent()) {
            Room room = roomOpt.get();
            room.setStatus("CLEANING");
            roomRepository.save(room);

            List<BookingDetail> details = bookingDetailRepository.findByRoomId(room.getId());
            String now = LocalDateTime.now().toString();
            Integer bookingId = null;

            for (BookingDetail detail : details) {
                if (detail.getCheckInActual() != null && detail.getCheckOutActual() == null) {
                    detail.setCheckOutActual(now);
                    bookingDetailRepository.save(detail);
                    if (detail.getBooking() != null) {
                        bookingId = detail.getBooking().getId();
                        detail.getBooking().setStatus("COMPLETED");
                        bookingRepository.save(detail.getBooking());
                    }
                    break;
                }
            }

            return ReceptionResponseDTO.builder()
                    .message("Check-out phòng " + room.getRoomNumber() + " thành công! Phòng đã chuyển sang trạng thái dọn dẹp.")
                    .bookingId(bookingId)
                    .roomId(room.getId())
                    .roomNumber(room.getRoomNumber())
                    .status("CLEANING")
                    .build();
        }

        throw new RuntimeException("Không tìm thấy đơn đặt phòng hoặc phòng có ID: " + id);
    }

    @Override
    @Transactional
    public ReceptionResponseDTO cleanComplete(Integer roomId) {
        if (roomId == null) {
            throw new IllegalArgumentException("Mã phòng (roomId) không được để trống!");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng có ID: " + roomId));

        if ("OCCUPIED".equalsIgnoreCase(room.getStatus())) {
            throw new IllegalArgumentException("Phòng " + room.getRoomNumber() + " đang có khách ở, không thể hoàn tất dọn dẹp!");
        }

        room.setStatus("AVAILABLE");
        roomRepository.save(room);

        return ReceptionResponseDTO.builder()
                .message("Phòng " + room.getRoomNumber() + " đã hoàn tất dọn dẹp, sẵn sàng đón khách!")
                .roomId(room.getId())
                .roomNumber(room.getRoomNumber())
                .status("AVAILABLE")
                .build();
    }

    @Override
    public List<ReceptionBookingDTO> getActiveReceptionBookings() {
        // Lấy danh sách booking mới nhất phục vụ màn hình lễ tân
        List<Booking> recentBookings = bookingRepository.findAll(
                PageRequest.of(0, 30, Sort.by(Sort.Direction.DESC, "id"))
        ).getContent();

        List<ReceptionBookingDTO> result = new ArrayList<>();
        for (Booking b : recentBookings) {
            List<BookingDetail> details = bookingDetailRepository.findByBookingId(b.getId());
            if (details.isEmpty()) {
                result.add(ReceptionBookingDTO.builder()
                        .bookingId(b.getId())
                        .bookingCode(b.getBookingCode())
                        .customerName(b.getCustomer() != null ? b.getCustomer().getFullName() : "N/A")
                        .customerPhone(b.getCustomer() != null ? b.getCustomer().getPhone() : "")
                        .status(b.getStatus())
                        .build());
            } else {
                for (BookingDetail bd : details) {
                    Room r = bd.getRoom();
                    result.add(ReceptionBookingDTO.builder()
                            .bookingId(b.getId())
                            .bookingCode(b.getBookingCode())
                            .customerName(b.getCustomer() != null ? b.getCustomer().getFullName() : "N/A")
                            .customerPhone(b.getCustomer() != null ? b.getCustomer().getPhone() : "")
                            .roomId(r != null ? r.getId() : null)
                            .roomNumber(r != null ? r.getRoomNumber() : "N/A")
                            .checkInExpected(bd.getCheckInExpected())
                            .checkOutExpected(bd.getCheckOutExpected())
                            .checkInActual(bd.getCheckInActual())
                            .checkOutActual(bd.getCheckOutActual())
                            .status(r != null && "CLEANING".equalsIgnoreCase(r.getStatus()) ? "CLEANING" : b.getStatus())
                            .build());
                }
            }
        }
        return result;
    }
}

package com.hotel.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateBookingRequest {
    private Long customerId;
    private LocalDateTime checkInExpected;
    private LocalDateTime checkOutExpected;
    private List<Long> roomIds; // Danh sách các id phòng khách chọn
    private String note;
}

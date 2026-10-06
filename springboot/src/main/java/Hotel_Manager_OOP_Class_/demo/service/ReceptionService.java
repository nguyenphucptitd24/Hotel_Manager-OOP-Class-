package Hotel_Manager_OOP_Class_.demo.service;

import Hotel_Manager_OOP_Class_.demo.dto.CheckInRequestDTO;
import Hotel_Manager_OOP_Class_.demo.dto.ReceptionBookingDTO;
import Hotel_Manager_OOP_Class_.demo.dto.ReceptionResponseDTO;

import java.util.List;

public interface ReceptionService {

    ReceptionResponseDTO checkIn(CheckInRequestDTO request);

    ReceptionResponseDTO checkInById(Integer bookingId);

    ReceptionResponseDTO checkOut(Integer id);

    ReceptionResponseDTO cleanComplete(Integer roomId);

    ReceptionResponseDTO deleteBooking(Integer bookingId);

    List<ReceptionBookingDTO> getActiveReceptionBookings();
}

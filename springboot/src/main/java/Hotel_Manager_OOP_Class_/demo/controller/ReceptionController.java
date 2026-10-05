package Hotel_Manager_OOP_Class_.demo.controller;

import Hotel_Manager_OOP_Class_.demo.dto.CheckInRequestDTO;
import Hotel_Manager_OOP_Class_.demo.dto.ReceptionBookingDTO;
import Hotel_Manager_OOP_Class_.demo.dto.ReceptionResponseDTO;
import Hotel_Manager_OOP_Class_.demo.security.All;
import Hotel_Manager_OOP_Class_.demo.service.ReceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reception")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReceptionController {

    private final ReceptionService receptionService;

    @All
    @PostMapping("/check-in")
    public ResponseEntity<ReceptionResponseDTO> checkIn(@RequestBody CheckInRequestDTO request) {
        ReceptionResponseDTO response = receptionService.checkIn(request);
        return ResponseEntity.ok(response);
    }

    @All
    @PostMapping("/check-in/{id}")
    public ResponseEntity<ReceptionResponseDTO> checkInById(@PathVariable Integer id) {
        ReceptionResponseDTO response = receptionService.checkInById(id);
        return ResponseEntity.ok(response);
    }

    @All
    @PostMapping("/check-out/{id}")
    public ResponseEntity<ReceptionResponseDTO> checkOut(@PathVariable Integer id) {
        ReceptionResponseDTO response = receptionService.checkOut(id);
        return ResponseEntity.ok(response);
    }

    @All
    @PostMapping("/clean-complete/{id}")
    public ResponseEntity<ReceptionResponseDTO> cleanComplete(@PathVariable Integer id) {
        ReceptionResponseDTO response = receptionService.cleanComplete(id);
        return ResponseEntity.ok(response);
    }

    @All
    @GetMapping("/bookings")
    public ResponseEntity<List<ReceptionBookingDTO>> getReceptionBookings() {
        List<ReceptionBookingDTO> list = receptionService.getActiveReceptionBookings();
        return ResponseEntity.ok(list);
    }
}

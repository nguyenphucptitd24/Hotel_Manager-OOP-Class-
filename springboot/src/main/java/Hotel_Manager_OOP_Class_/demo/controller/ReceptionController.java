package Hotel_Manager_OOP_Class.controller;

import Hotel_Manager_OOP_Class.dto.CheckInRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reception")
@CrossOrigin(origins = "*")
public class ReceptionController {

    @PostMapping("/check-in")
    public ResponseEntity<?> checkIn(@RequestBody CheckInRequestDTO request) {
        return ResponseEntity.ok(Map.of(
            "message", "Check-in thành công!",
            "bookingId", request.getBookingId(),
            "status", "CHECKED_IN"
        ));
    }

    @PostMapping("/check-in/{id}")
    public ResponseEntity<?> checkInById(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("message", "Check-in thành công!", "bookingId", id, "status", "CHECKED_IN"));
    }

    @PostMapping("/check-out/{id}")
    public ResponseEntity<?> checkOut(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("message", "Check-out thành công!", "bookingId", id, "status", "CLEANING"));
    }

    @PostMapping("/clean-complete/{id}")
    public ResponseEntity<?> cleanComplete(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("message", "Phòng đã sẵn sàng!", "roomId", id, "status", "AVAILABLE"));
    }
}
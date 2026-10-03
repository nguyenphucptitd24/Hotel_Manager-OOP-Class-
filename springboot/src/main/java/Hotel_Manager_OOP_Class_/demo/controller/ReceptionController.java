package Hotel_Manager_OOP_Class_.demo.controller;

import Hotel_Manager_OOP_Class_.demo.dto.CheckInRequestDTO;
import Hotel_Manager_OOP_Class_.demo.service.ReceptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reception")
@CrossOrigin(origins = "*")
public class ReceptionController {

    @Autowired
    private ReceptionService receptionService;

    @PostMapping("/check-in")
    public ResponseEntity<?> checkIn(@RequestBody CheckInRequestDTO request) {
        return ResponseEntity.ok(receptionService.checkIn(request));
    }

    @PostMapping("/check-out/{id}")
    public ResponseEntity<?> checkOut(@PathVariable Integer id) {
        return ResponseEntity.ok(receptionService.checkOut(id));
    }

    @PostMapping("/clean-complete/{id}")
    public ResponseEntity<?> cleanComplete(@PathVariable Integer id) {
        return ResponseEntity.ok(receptionService.cleanComplete(id));
    }
}
package Hotel_Manager_OOP_Class_.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Hotel_Manager_OOP_Class_.demo.dto.RoomTypeResponse;
import Hotel_Manager_OOP_Class_.demo.entity.RoomType;
import Hotel_Manager_OOP_Class_.demo.security.AdminOnly;
import Hotel_Manager_OOP_Class_.demo.security.All;
import Hotel_Manager_OOP_Class_.demo.service.RoomTypeService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/room-types")
@RequiredArgsConstructor
public class RoomTypeController {

    private final RoomTypeService roomTypeService;
    @All
    @GetMapping
    public List<RoomTypeResponse> getAllRoomTypes() {
        return roomTypeService.getAllRoomTypes();
    }
    @All
    @GetMapping("/{id}")
    public RoomTypeResponse getRoomTypeById(@PathVariable Integer id) {
        return roomTypeService.getRoomTypeById(id);
    }
    @AdminOnly
    @PostMapping
    public RoomTypeResponse createRoomType(@RequestBody RoomType roomType) {
        return roomTypeService.createRoomType(roomType);
    }
    @AdminOnly
    @PutMapping("/{id}")
    public RoomTypeResponse updateRoomType(
            @PathVariable Integer id,
            @RequestBody RoomType roomType) {

        return roomTypeService.updateRoomType(id, roomType);
    }
    @AdminOnly
    @DeleteMapping("/{id}")
    public void deleteRoomType(@PathVariable Integer id) {
        roomTypeService.deleteRoomType(id);
    }
}
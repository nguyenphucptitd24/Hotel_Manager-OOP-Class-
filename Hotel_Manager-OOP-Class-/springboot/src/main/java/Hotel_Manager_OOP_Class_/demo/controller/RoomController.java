package Hotel_Manager_OOP_Class_.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Hotel_Manager_OOP_Class_.demo.dto.RoomResponse;
import Hotel_Manager_OOP_Class_.demo.entity.Room;
import Hotel_Manager_OOP_Class_.demo.security.AdminOnly;
import Hotel_Manager_OOP_Class_.demo.security.All;
import Hotel_Manager_OOP_Class_.demo.service.RoomService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    @All
    @GetMapping
    public List<RoomResponse> searchRooms(
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer roomTypeId) {

        return roomService.searchRooms(
                roomNumber,
                floor,
                status,
                roomTypeId
        );
    }
    @All
    @GetMapping("/{id}")
    public RoomResponse getRoomById(@PathVariable Integer id) {
        return roomService.getRoomById(id);
    }
    
    @AdminOnly
    @PostMapping
    public RoomResponse createRoom(@RequestBody Room room) {
        return roomService.createRoom(room);
    }
    @AdminOnly
    @PutMapping("/{id}")
    public RoomResponse updateRoom(
            @PathVariable Integer id,
            @RequestBody Room room) {

        return roomService.updateRoom(id, room);
    }
    @AdminOnly
    @DeleteMapping("/{id}")
    public void deleteRoom(@PathVariable Integer id) {
        roomService.deleteRoom(id);
    }
    @All
    @PatchMapping("/{id}/status")
    public RoomResponse updateRoomStatus(
            @PathVariable Integer id,
            @RequestParam String status) {

        return roomService.updateRoomStatus(id, status);
    }
    
} 
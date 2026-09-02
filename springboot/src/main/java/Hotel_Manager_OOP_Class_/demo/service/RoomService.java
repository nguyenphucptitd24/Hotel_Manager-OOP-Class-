package Hotel_Manager_OOP_Class_.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import Hotel_Manager_OOP_Class_.demo.dto.RoomResponse;
import Hotel_Manager_OOP_Class_.demo.entity.Room;
import Hotel_Manager_OOP_Class_.demo.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    public List<RoomResponse> searchRooms(
        String roomNumber,
        Integer floor,
        String status,
        Integer roomTypeId) {
        return roomRepository.searchRooms(
                roomNumber,
                floor,
                status,
                roomTypeId
        )
        .stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
    }
    private RoomResponse toResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getRoomNumber(),
                room.getFloor(),
                room.getStatus(),
                room.getRoomType().getId(),
                room.getRoomType().getName(),
                room.getRoomType().getBasePrice()
        );
    }
    public RoomResponse getRoomById(Integer id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng"));
        return new RoomResponse(
                room.getId(),
                room.getRoomNumber(),
                room.getFloor(),
                room.getStatus(),
                room.getRoomType().getId(),
                room.getRoomType().getName(),
                room.getRoomType().getBasePrice()
        );
    }
    @Transactional
    public RoomResponse createRoom(Room room) {
        if (roomRepository.existsByRoomNumber(room.getRoomNumber())) {
            throw new RuntimeException("Số phòng đã tồn tại");
        }

        if (room.getId() == null) {
            room.setId(roomRepository.findNextId());
        }

        Room savedRoom = roomRepository.save(room);
        return toResponse(savedRoom);
    }

    @Transactional
    public RoomResponse updateRoom(Integer id, Room room) {
        Room existingRoom = roomRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng"));

        if (roomRepository.existsByRoomNumberAndIdNot(room.getRoomNumber(), id)) {
            throw new RuntimeException("Số phòng đã tồn tại");
        }

        existingRoom.setRoomNumber(room.getRoomNumber());
        existingRoom.setFloor(room.getFloor());
        existingRoom.setStatus(room.getStatus());
        existingRoom.setRoomType(room.getRoomType());

        Room updatedRoom = roomRepository.save(existingRoom);
        return toResponse(updatedRoom);
    }
    public void deleteRoom(Integer id) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy phòng");
        }

        roomRepository.deleteById(id);
    }
    public RoomResponse updateRoomStatus(Integer id, String status) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng"));
        String currentStatus = room.getStatus();
        boolean valid = switch (currentStatus) {
            case "AVAILABLE" -> status.equals("OCCUPIED");
            case "OCCUPIED" -> status.equals("CLEANING");
            case "CLEANING" -> status.equals("AVAILABLE");
            default -> false;
        };
        if (!valid) {
            throw new RuntimeException(
                    "Không thể chuyển trạng thái từ "
                    + currentStatus + " sang " + status
            );
        }
        room.setStatus(status);
        Room updatedRoom = roomRepository.save(room);
        return new RoomResponse(
                updatedRoom.getId(),
                updatedRoom.getRoomNumber(),
                updatedRoom.getFloor(),
                updatedRoom.getStatus(),
                updatedRoom.getRoomType().getId(),
                updatedRoom.getRoomType().getName(),
                updatedRoom.getRoomType().getBasePrice()
        );
    }
}

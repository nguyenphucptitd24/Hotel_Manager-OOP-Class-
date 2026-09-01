package Hotel_Manager_OOP_Class_.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import Hotel_Manager_OOP_Class_.demo.dto.RoomTypeResponse;
import Hotel_Manager_OOP_Class_.demo.entity.RoomType;
import Hotel_Manager_OOP_Class_.demo.repository.RoomTypeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

    public List<RoomTypeResponse> getAllRoomTypes() {
        return roomTypeRepository.findAll()
            .stream()
            .map(roomType -> new RoomTypeResponse(
                    roomType.getId(),
                    roomType.getName(),
                    roomType.getBasePrice(),
                    roomType.getCapacity(),
                    roomType.getDescription()
            ))
            .toList();
    }
    public RoomTypeResponse getRoomTypeById(Integer id) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại phòng"));

        return new RoomTypeResponse(
                roomType.getId(),
                roomType.getName(),
                roomType.getBasePrice(),
                roomType.getCapacity(),
                roomType.getDescription()
        );
    }

    public RoomTypeResponse createRoomType(RoomType roomType) {
        RoomType savedRoomType = roomTypeRepository.save(roomType);
        return new RoomTypeResponse(
                savedRoomType.getId(),
                savedRoomType.getName(),
                savedRoomType.getBasePrice(),
                savedRoomType.getCapacity(),
                savedRoomType.getDescription()
        );
    }
    public RoomTypeResponse updateRoomType(Integer id, RoomType roomType) {
        RoomType existingRoomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại phòng"));
        existingRoomType.setName(roomType.getName());
        existingRoomType.setBasePrice(roomType.getBasePrice());
        existingRoomType.setCapacity(roomType.getCapacity());
        existingRoomType.setDescription(roomType.getDescription());
        RoomType updatedRoomType = roomTypeRepository.save(existingRoomType);
        return new RoomTypeResponse(
                updatedRoomType.getId(),
                updatedRoomType.getName(),
                updatedRoomType.getBasePrice(),
                updatedRoomType.getCapacity(),
                updatedRoomType.getDescription()
        );
    }
    public void deleteRoomType(Integer id) {
        if (!roomTypeRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy loại phòng");
        }

        roomTypeRepository.deleteById(id);
    }
}
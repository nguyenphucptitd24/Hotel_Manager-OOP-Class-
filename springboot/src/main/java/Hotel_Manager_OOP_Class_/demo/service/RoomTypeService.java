package Hotel_Manager_OOP_Class_.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import Hotel_Manager_OOP_Class_.demo.dto.RoomTypeResponse;
import Hotel_Manager_OOP_Class_.demo.entity.RoomType;
import Hotel_Manager_OOP_Class_.demo.repository.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

    public List<RoomTypeResponse> getAllRoomTypes() {
        return roomTypeRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
    }
    public RoomTypeResponse getRoomTypeById(Integer id) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại phòng"));

        return toResponse(roomType);
    }

    @Transactional
    public RoomTypeResponse createRoomType(RoomType roomType) {
        if (roomTypeRepository.existsByName(roomType.getName())) {
            throw new RuntimeException("Tên loại phòng đã tồn tại");
        }

        if (roomType.getId() == null) {
            roomType.setId(roomTypeRepository.findNextId());
        }

        RoomType savedRoomType = roomTypeRepository.save(roomType);
        return toResponse(savedRoomType);
    }

    @Transactional
    public RoomTypeResponse updateRoomType(Integer id, RoomType roomType) {
        RoomType existingRoomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại phòng"));

        if (roomTypeRepository.existsByNameAndIdNot(roomType.getName(), id)) {
            throw new RuntimeException("Tên loại phòng đã tồn tại");
        }

        existingRoomType.setName(roomType.getName());
        existingRoomType.setBasePrice(roomType.getBasePrice());
        existingRoomType.setCapacity(roomType.getCapacity());
        existingRoomType.setDescription(roomType.getDescription());
        RoomType updatedRoomType = roomTypeRepository.save(existingRoomType);
        return toResponse(updatedRoomType);
    }
    public void deleteRoomType(Integer id) {
        if (!roomTypeRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy loại phòng");
        }

        roomTypeRepository.deleteById(id);
    }

    private RoomTypeResponse toResponse(RoomType roomType) {
        return new RoomTypeResponse(
                roomType.getId(),
                roomType.getName(),
                roomType.getBasePrice(),
                roomType.getCapacity(),
                roomType.getDescription()
        );
    }
}

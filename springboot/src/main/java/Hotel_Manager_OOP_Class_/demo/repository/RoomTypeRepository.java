package Hotel_Manager_OOP_Class_.demo.repository;

import Hotel_Manager_OOP_Class_.demo.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {
}

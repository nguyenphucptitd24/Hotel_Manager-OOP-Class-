package Hotel_Manager_OOP_Class_.demo.repository;

import Hotel_Manager_OOP_Class_.demo.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer id);

    @Query("SELECT COALESCE(MAX(rt.id), 0) + 1 FROM RoomType rt")
    Integer findNextId();
}

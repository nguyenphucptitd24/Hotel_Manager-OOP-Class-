package Hotel_Manager_OOP_Class_.demo.repository;

import Hotel_Manager_OOP_Class_.demo.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    List<Room> findByRoomNumber(String roomNumber);

    boolean existsByRoomNumber(String roomNumber);

    boolean existsByRoomNumberAndIdNot(String roomNumber, Integer id);

    @Query("SELECT COALESCE(MAX(r.id), 0) + 1 FROM Room r")
    Integer findNextId();

    List<Room> findByStatus(String status);

    List<Room> findByFloor(Integer floor);

    @Query(value = """
            SELECT r.*
            FROM rooms r
            WHERE r.id NOT IN (
                SELECT bd.room_id
                FROM booking_details bd
                INNER JOIN bookings b ON b.id = bd.booking_id
                WHERE CONVERT(date, bd.check_in_expected) < CONVERT(date, :checkOut)
                  AND CONVERT(date, bd.check_out_expected) > CONVERT(date, :checkIn)
            )
            ORDER BY r.id
            """, nativeQuery = true)
    List<Room> findAvailableRooms(@Param("checkIn") String checkIn,
                                 @Param("checkOut") String checkOut);
    @Query("""
        SELECT r FROM Room r
        WHERE (:roomNumber IS NULL OR r.roomNumber = :roomNumber)
        AND (:floor IS NULL OR r.floor = :floor)
        AND (:status IS NULL OR r.status = :status)
        AND (:roomTypeId IS NULL OR r.roomType.id = :roomTypeId)
    """)
    List<Room> searchRooms(
            @Param("roomNumber") String roomNumber,
            @Param("floor") Integer floor,
            @Param("status") String status,
            @Param("roomTypeId") Integer roomTypeId
    );
}

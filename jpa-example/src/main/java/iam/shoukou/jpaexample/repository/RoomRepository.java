package iam.shoukou.jpaexample.repository;

import iam.shoukou.jpaexample.model.OpenRoom;
import iam.shoukou.jpaexample.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("select r from Room r where r.name = :name")
    Optional<Room> findByName(String name);

    @Query("select r from Room r where r.type = 'OPEN'")
    List<OpenRoom> findAllOpenRoom();

    @Query("select r from Room r where r.type = :type")
    List<Room> findRoomsByType(String type);
}

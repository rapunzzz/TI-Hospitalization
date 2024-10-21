package apap.ti.hospitalization2206082801.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;

import apap.ti.hospitalization2206082801.model.Room;

@Repository
public interface RoomDb extends JpaRepository<Room, String> {
    List<Room> findByDeletedAtIsNull(Sort sort);
}

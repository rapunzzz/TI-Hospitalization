package apap.ti.hospitalization2206082801.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import apap.ti.hospitalization2206082801.model.Reservation;

@Repository
public interface ReservationDb extends JpaRepository<Reservation, String>{
    List<Reservation> findByDeletedAtIsNull(Sort sort);
}

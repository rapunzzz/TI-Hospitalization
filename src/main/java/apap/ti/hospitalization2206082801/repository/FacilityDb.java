package apap.ti.hospitalization2206082801.repository;

import java.util.List;
import java.util.UUID;
import apap.ti.hospitalization2206082801.model.Facility;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityDb extends JpaRepository<Facility, UUID>{
    List<Facility> findByDeletedAtIsNull(Sort sort);
}

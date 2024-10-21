package apap.ti.hospitalization2206082801.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import apap.ti.hospitalization2206082801.model.Nurse;

@Repository
public interface NurseDb extends JpaRepository<Nurse, UUID> {
    List<Nurse> findByDeletedAtIsNull(Sort sort);
}

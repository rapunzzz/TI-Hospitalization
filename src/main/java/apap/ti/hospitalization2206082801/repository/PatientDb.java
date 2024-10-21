package apap.ti.hospitalization2206082801.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import apap.ti.hospitalization2206082801.model.Patient;

@Repository
public interface PatientDb extends JpaRepository<Patient, UUID> {
    List<Patient> findByDeletedAtIsNull(Sort sort);
}

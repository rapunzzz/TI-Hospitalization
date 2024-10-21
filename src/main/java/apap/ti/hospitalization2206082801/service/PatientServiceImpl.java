package apap.ti.hospitalization2206082801.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import apap.ti.hospitalization2206082801.model.Patient;
import apap.ti.hospitalization2206082801.repository.PatientDb;

@Service
public class PatientServiceImpl implements PatientService {
    @Autowired
    PatientDb patientDb;

    @Override
    public Patient addPatient(Patient patient) {
        return patientDb.save(patient);
    }

    @Override
    public List<Patient> getAllPatient() {
        Sort.Order orderByPatientId = Sort.Order.by("id").ignoreCase();
        Sort.Order orderByNama = Sort.Order.by("name").ignoreCase();
        Sort sort = Sort.by(orderByPatientId).and(Sort.by(orderByNama));
        return patientDb.findByDeletedAtIsNull(sort);
    }

    @Override
    public Patient getPatientById(UUID id) {
        for (Patient patient : getAllPatient()) {
            if (patient.getId().equals(id)) {
                return patient;
            }
        }
        return null;
    }

    @Override
    public Patient getPatientByNik(String nik) {
        for (Patient patient : getAllPatient()) {
            if (patient.getNIK().equals(nik)) {
                return patient;
            }
        }
        return null;
    }
}

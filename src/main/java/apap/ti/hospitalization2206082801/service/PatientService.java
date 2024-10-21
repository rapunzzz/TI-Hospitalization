package apap.ti.hospitalization2206082801.service;

import java.util.List;
import java.util.UUID;

import apap.ti.hospitalization2206082801.model.Patient;

public interface PatientService {
    Patient addPatient(Patient patient);
    List<Patient> getAllPatient();
    Patient getPatientById(UUID id);
    Patient getPatientByNik(String nik);
    // void deletePatient(Patient patient);
    // Patient updatePatient(Patient patient);
}

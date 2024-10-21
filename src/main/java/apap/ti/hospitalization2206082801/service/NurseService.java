package apap.ti.hospitalization2206082801.service;

import java.util.List;

import java.util.UUID;

import apap.ti.hospitalization2206082801.model.Nurse;

public interface NurseService {
    Nurse addNurse(Nurse nurse);
    List<Nurse> getAllNurse();
    Nurse getNurseById(UUID id);
}

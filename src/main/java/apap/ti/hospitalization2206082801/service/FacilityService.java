package apap.ti.hospitalization2206082801.service;

import java.util.List;
import java.util.UUID;

import apap.ti.hospitalization2206082801.model.Facility;

public interface FacilityService {
    Facility addFacility(Facility facility);
    List<Facility> getAllFacility();
    Facility getFacilityById(UUID id);
}

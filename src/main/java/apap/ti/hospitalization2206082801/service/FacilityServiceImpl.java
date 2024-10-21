package apap.ti.hospitalization2206082801.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import apap.ti.hospitalization2206082801.model.Facility;
import apap.ti.hospitalization2206082801.repository.FacilityDb;

@Service
public class FacilityServiceImpl implements FacilityService{
    @Autowired
    FacilityDb facilityDb;

    @Override
    public Facility addFacility(Facility facility) {
        return facilityDb.save(facility);
    }
    @Override
    public List<Facility> getAllFacility() {
        Sort.Order orderByFacilityId = Sort.Order.by("id").ignoreCase();
        Sort.Order orderByNama = Sort.Order.by("name").ignoreCase();
        Sort sort = Sort.by(orderByFacilityId).and(Sort.by(orderByNama));
        return facilityDb.findByDeletedAtIsNull(sort);
    }

    @Override
    public Facility getFacilityById(UUID id) {
        for (Facility facility : getAllFacility()) {
            if (facility.getId().equals(id)) {
                return facility;
            }
        }
        return null;
    }
}

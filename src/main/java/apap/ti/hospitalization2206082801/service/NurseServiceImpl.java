package apap.ti.hospitalization2206082801.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import apap.ti.hospitalization2206082801.model.Nurse;
import apap.ti.hospitalization2206082801.repository.NurseDb;

@Service
public class NurseServiceImpl implements NurseService {
    @Autowired
    NurseDb nurseDb;

    @Override
    public Nurse addNurse(Nurse nurse) {
        return nurseDb.save(nurse);
    }

    @Override
    public List<Nurse> getAllNurse() {
        Sort.Order orderByNurseId = Sort.Order.by("id").ignoreCase();
        Sort.Order orderByNama = Sort.Order.by("name").ignoreCase();
        Sort sort = Sort.by(orderByNurseId).and(Sort.by(orderByNama));
        return nurseDb.findByDeletedAtIsNull(sort);
    }

    @Override
    public Nurse getNurseById(UUID id) {
        for (Nurse nurse : getAllNurse()) {
            if (nurse.getId().equals(id)) {
                return nurse;
            }
        }
        return null;
    }
}

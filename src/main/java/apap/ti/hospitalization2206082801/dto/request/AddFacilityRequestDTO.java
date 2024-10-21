package apap.ti.hospitalization2206082801.dto.request;

import java.util.List;

import apap.ti.hospitalization2206082801.model.Facility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddFacilityRequestDTO {
    private List<Facility> selectedFacilityIds; // ID fasilitas yang dipilih
    private double totalFee; 
}

package apap.ti.hospitalization2206082801.restdto.response;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReservationResponseDTO {
    private String id; 

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateIn;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOut;

    private double totalFee;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PatientResponseDTO patient; // Buat DTO untuk Patient jika belum ada

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private NurseResponseDTO assignedNurse; // Buat DTO untuk Nurse jika belum ada

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private RoomResponseDTO room; // Buat DTO untuk Room jika belum ada

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FacilityResponseDTO> facilities; // Buat DTO untuk Facility jika belum ada

    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Asia/Jakarta")
    private Date createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Asia/Jakarta")
    private Date updatedAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date deletedAt;
}

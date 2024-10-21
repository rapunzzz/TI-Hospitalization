package apap.ti.hospitalization2206082801.dto.request;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;

import apap.ti.hospitalization2206082801.model.Facility;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddReservationRequestDTO {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Tanggal check-in tidak boleh kosong")
    private Date dateIn;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Tanggal check-out tidak boleh kosong")
    private Date dateOut;

    @NotNull(message = "ID pasien tidak boleh kosong")
    private UUID patientId;

    private UUID assignedNurseId;

    private String roomId;

    private double totalFee;

    private List<Facility> facilities;

}

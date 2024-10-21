package apap.ti.hospitalization2206082801.restdto.response;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import apap.ti.hospitalization2206082801.model.Reservation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PatientResponseDTO {
    private UUID id;

    private String NIK;

    private String name;

    private String email;

    private Date birthDate;

    private boolean gender;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Reservation> reservations;

    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Asia/Jakarta")
    private Date createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Asia/Jakarta")
    private Date updatedAt;
}

package apap.ti.hospitalization2206082801.dto.request;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.*;

import apap.ti.hospitalization2206082801.model.Reservation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddPatientRequestDTO {
    @NotBlank(message = "NIK tidak boleh kosong")
    private String NIK;

    @NotBlank(message = "Nama patient tidak boleh kosong")
    private String name;

    @NotBlank(message = "Email tidak boleh kosong")
    private String email;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    private Boolean gender;

    private List<Reservation> reservations;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updatedAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date deletedAt;
}

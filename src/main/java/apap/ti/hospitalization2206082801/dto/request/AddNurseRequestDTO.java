package apap.ti.hospitalization2206082801.dto.request;

import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddNurseRequestDTO {
    @NotBlank(message = "Nama nurse tidak boleh kosong")
    private String name;

    @NotBlank(message = "Email tidak boleh kosong")
    private String email;

    private boolean gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updatedAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date deletedAt;
}

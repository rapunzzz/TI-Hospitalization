package apap.ti.hospitalization2206082801.dto.request;

import java.util.List;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

import apap.ti.hospitalization2206082801.model.Reservation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddRoomRequestDTO {
    @NotBlank(message = "Nama room tidak boleh kosong")
    private String name;

    @NotBlank(message = "Deskripsi room tidak boleh kosong")
    private String description;

    private int maxCapacity;

    private double pricePerDay;

    private List<Reservation> reservations;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updatedAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date deletedAt;
}

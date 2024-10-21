package apap.ti.hospitalization2206082801.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Data
public class UpdateReservationRequestDTO extends AddReservationRequestDTO {
    @NotNull(message = "ID Reservation tidak boleh kosong")
    private String id;
}

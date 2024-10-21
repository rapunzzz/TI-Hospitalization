package apap.ti.hospitalization2206082801.dto.request;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Data
public class UpdateRoomRequestDTO extends AddRoomRequestDTO{
    @NotNull(message = "ID room tidak boleh kosong")
    private String id;
}


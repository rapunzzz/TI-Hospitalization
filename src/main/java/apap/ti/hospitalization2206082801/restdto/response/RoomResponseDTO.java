package apap.ti.hospitalization2206082801.restdto.response;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import apap.ti.hospitalization2206082801.model.Reservation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoomResponseDTO {
    private String id;

    private String name;

    private String description;

    private int maxCapacity;

    private double pricePerDay;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Reservation> reservations;

    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Asia/Jakarta")
    private Date createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Asia/Jakarta")
    private Date updatedAt;
}

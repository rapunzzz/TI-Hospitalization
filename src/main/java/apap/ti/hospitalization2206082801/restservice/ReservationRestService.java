package apap.ti.hospitalization2206082801.restservice;

import java.util.List;
import java.util.Map;

import apap.ti.hospitalization2206082801.restdto.response.ReservationResponseDTO;

public interface ReservationRestService {
    List<ReservationResponseDTO> getAllReservations();
    ReservationResponseDTO getReservationById(String id);
    List<Map<String, Object>>  getAllReservationsToChart(String period, int year);
}

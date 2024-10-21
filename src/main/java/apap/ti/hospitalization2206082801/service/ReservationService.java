package apap.ti.hospitalization2206082801.service;

import java.util.List;
import apap.ti.hospitalization2206082801.model.Reservation;
import apap.ti.hospitalization2206082801.restdto.response.ReservationResponseDTO;

public interface ReservationService {
    List<Reservation> getAllReservations();
    List<Reservation> getAllReservationsWithSoftDelete();
    List<ReservationResponseDTO> getAllReservationFromRest() throws Exception;
    ReservationResponseDTO getReservationByIdFromRest(String id) throws Exception;
    List<ReservationResponseDTO> getAllReservationFromRestToChart(String period, int year) throws Exception;
    Reservation addReservation(Reservation reservation);
    Reservation getReservationById(String id);
    void deleteReservation(Reservation reservation);
    Reservation updateReservation(Reservation reservation);
}

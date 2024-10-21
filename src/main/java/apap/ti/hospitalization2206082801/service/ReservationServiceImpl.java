package apap.ti.hospitalization2206082801.service;

import java.util.Date;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import apap.ti.hospitalization2206082801.model.Reservation;
import apap.ti.hospitalization2206082801.repository.ReservationDb;
import apap.ti.hospitalization2206082801.restdto.response.BaseResponseDTO;
import apap.ti.hospitalization2206082801.restdto.response.ReservationResponseDTO;

@Service
public class ReservationServiceImpl implements ReservationService{
    
    private final ReservationDb reservationDb;
    private final WebClient webClient;

    // Constructor Injection
    public ReservationServiceImpl(ReservationDb reservationDb, WebClient.Builder webClientBuilder) {
        this.reservationDb = reservationDb;
        this.webClient = webClientBuilder
                            .baseUrl("http://localhost:8080/api")
                            .build();
    }

    @Override
    public List<Reservation> getAllReservations() {
        // Sort by createdAt in descending order
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return reservationDb.findByDeletedAtIsNull(sort);
    }

    @Override
    public List<Reservation> getAllReservationsWithSoftDelete() {
        return reservationDb.findAll();
    }

    @Override 
    public Reservation addReservation(Reservation reservation) {
        if (reservationDb.existsById(reservation.getId())) {
            throw new IllegalArgumentException("Reservation already exists");
        }
        return reservationDb.save(reservation);
    }

    @Override
    public Reservation getReservationById(String id) {
        for (Reservation reservation : getAllReservations()) {
            if (reservation.getId().equals(id)) {
                return reservation;
            }
        }
        return null;
    }

    @Override
    public List<ReservationResponseDTO> getAllReservationFromRest() throws Exception {
        var response = webClient
            .get()
            .uri("/reservations")
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<List<ReservationResponseDTO>>>() {})
            .block();

        if (response == null) {
            throw new Exception("Failed consume API getAllReservation");
        }

        if (response.getStatus() != 200) {
            throw new Exception(response.getMessage());
        }

        return response.getData();
    }

    @Override
    public ReservationResponseDTO getReservationByIdFromRest(String id) throws Exception {
        var response = webClient
            .get()
            .uri("/reservations/" + id)  // Menggunakan ID pada URI
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<ReservationResponseDTO>>() {}) // Mengubah tipe data untuk satu reservation
            .block();

        if (response == null) {
            throw new Exception("Failed to consume API getReservationById");
        }

        if (response.getStatus() != 200) {
            throw new Exception(response.getMessage());
        }

        return response.getData(); // Mengembalikan detail reservation yang diminta
    }

    @Override
    public void deleteReservation(Reservation reservation) {
        reservation.setDeletedAt(new Date());
        reservationDb.save(reservation);
    }

    @Override
    public Reservation updateReservation(Reservation reservation) {
        Reservation getReservation = getReservationById(reservation.getId());
        if (getReservation != null) {
            getReservation.setId(reservation.getId());
            getReservation.setAssignedNurse(reservation.getAssignedNurse());
            getReservation.setDateIn(reservation.getDateIn());
            getReservation.setDateOut(reservation.getDateOut());
            getReservation.setPatient(reservation.getPatient());
            getReservation.setRoom(reservation.getRoom());
            getReservation.setTotalFee(reservation.getTotalFee());
            getReservation.setFacilities(reservation.getFacilities());
            getReservation.setUpdatedAt(reservation.getUpdatedAt());
            reservationDb.save(getReservation);
            return getReservation;
        }
        return null;
    }

    @Override
    public List<ReservationResponseDTO> getAllReservationFromRestToChart(String period, int year) throws Exception {
        var response = webClient
            .get()
            .uri(uriBuilder -> uriBuilder
                .path("/reservations/chart")
                .queryParam("period", period)
                .queryParam("year", year)
                .build())
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<List<ReservationResponseDTO>>>() {})
            .block();

        if (response == null) {
            throw new Exception("Failed to consume API getAllReservation");
        }

        if (response.getStatus() != 200) {
            throw new Exception(response.getMessage());
        }

        return response.getData();
    }

}

package apap.ti.hospitalization2206082801.restcontroller;

import apap.ti.hospitalization2206082801.restdto.response.BaseResponseDTO;

import apap.ti.hospitalization2206082801.restdto.response.ReservationResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import apap.ti.hospitalization2206082801.restservice.ReservationRestService;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@Getter
@Setter
@RequestMapping("/api/reservations")
public class ReservationRestController {

    private final ReservationRestService reservationRestService;

    public ReservationRestController(ReservationRestService reservationRestService) {
        this.reservationRestService = reservationRestService;
    }

    @GetMapping("")
    public ResponseEntity<?> listReservation() {
        var baseResponseDTO = new BaseResponseDTO<List<ReservationResponseDTO>>();
        List<ReservationResponseDTO> listReservation = reservationRestService.getAllReservations();

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(listReservation);
        baseResponseDTO.setMessage(String.format("List reservation berhasil ditemukan"));
        baseResponseDTO.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/chart") // Perbaiki mapping agar sesuai
    public ResponseEntity<?> listReservationToChart(@RequestParam String period, @RequestParam int year) {
        var baseResponseDTO = new BaseResponseDTO<List<Map<String, Object>>>();
        
        // Memanggil service untuk mendapatkan data reservasi berdasarkan periode dan tahun
        List<Map<String, Object>> listReservation = reservationRestService.getAllReservationsToChart(period, year);

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(listReservation);
        baseResponseDTO.setMessage(String.format("List reservation untuk chart berhasil ditemukan"));
        baseResponseDTO.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> reservationById(@PathVariable("id") String id) {
        var baseResponseDTO = new BaseResponseDTO<ReservationResponseDTO>();

        ReservationResponseDTO reservation = reservationRestService.getReservationById(id);
        if(reservation == null) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage(String.format("Data reservation tidak ditemukan"));
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(reservation);
        baseResponseDTO.setMessage(String.format("Reservation dengan ID %s berhasil ditemukan", reservation.getId()));
        baseResponseDTO.setTimestamp(new Date());

        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

    }


}

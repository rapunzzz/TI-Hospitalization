package apap.ti.hospitalization2206082801.restservice;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import apap.ti.hospitalization2206082801.model.Facility;
import apap.ti.hospitalization2206082801.model.Nurse;
import apap.ti.hospitalization2206082801.model.Patient;
import apap.ti.hospitalization2206082801.model.Reservation;
import apap.ti.hospitalization2206082801.model.Room;
import apap.ti.hospitalization2206082801.repository.ReservationDb;
import apap.ti.hospitalization2206082801.restdto.response.FacilityResponseDTO;
import apap.ti.hospitalization2206082801.restdto.response.NurseResponseDTO;
import apap.ti.hospitalization2206082801.restdto.response.PatientResponseDTO;
import apap.ti.hospitalization2206082801.restdto.response.ReservationResponseDTO;
import apap.ti.hospitalization2206082801.restdto.response.RoomResponseDTO;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReservationRestServiceImpl implements ReservationRestService{
    private final ReservationDb reservationDb;

    // Constructor Dependency Injection
    public ReservationRestServiceImpl(ReservationDb reservationDb) {
        this.reservationDb = reservationDb;
    }

    @Override
    public List<ReservationResponseDTO> getAllReservations() {
        var listReservation = reservationDb.findAll();
        var listReservationResponseDTO = new ArrayList<ReservationResponseDTO>();
        listReservation.forEach(reservation -> {
            var reservationResponseDTO = reservationToReservationResponseDTO(reservation);
            listReservationResponseDTO.add(reservationResponseDTO);
        });

        return listReservationResponseDTO;
    }

    @Override
    public ReservationResponseDTO getReservationById(String id) {
        var reservation = reservationDb.findById(id).orElse(null);
        
        if (reservation == null) {
            return null;
        }
        
        // Konversi reservation menjadi ReservationResponseDTO
        return reservationToReservationResponseDTO(reservation);
    }

    @Override
    public List<Map<String, Object>> getAllReservationsToChart(String period, int year) {
        var listReservation = reservationDb.findAll();
        var reservationMap = new HashMap<Integer, List<ReservationResponseDTO>>();

        // Mengelompokkan data berdasarkan periode
        listReservation.forEach(reservation -> {
            // Mengambil tanggal dari reservasi
            Date createdAt = reservation.getCreatedAt();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(createdAt); // Set waktu pada calendar

            int reservationYear = calendar.get(Calendar.YEAR); // Mendapatkan tahun
            int reservationMonth = calendar.get(Calendar.MONTH) + 1; // Mendapatkan bulan (0-11, jadi tambah 1)
            
            // Pastikan reservasi berada di tahun yang dipilih
            if (reservationYear == year) {
                var reservationResponseDTO = reservationToReservationResponseDTO(reservation);

                if ("monthly".equalsIgnoreCase(period)) {
                    // Kelompokkan berdasarkan bulan
                    List<ReservationResponseDTO> reservationList;
                    
                    // Periksa apakah bulan sudah ada dalam map
                    if (reservationMap.containsKey(reservationMonth)) {
                        // Jika ada, ambil list yang sudah ada
                        reservationList = reservationMap.get(reservationMonth);
                    } else {
                        // Jika tidak ada, buat list baru
                        reservationList = new ArrayList<>();
                        reservationMap.put(reservationMonth, reservationList); // Simpan list baru dalam map
                    }

                    // Tambahkan reservationResponseDTO ke dalam list
                    reservationList.add(reservationResponseDTO);

                } else if ("quarterly".equalsIgnoreCase(period)) {
                    // Kelompokkan berdasarkan kuartal
                    int quarter = (reservationMonth - 1) / 3 + 1; // Menghitung kuartal
                    List<ReservationResponseDTO> reservationList;

                    // Periksa apakah kuartal sudah ada dalam map
                    if (reservationMap.containsKey(quarter)) {
                        // Jika ada, ambil list yang sudah ada
                        reservationList = reservationMap.get(quarter);
                    } else {
                        // Jika tidak ada, buat list baru
                        reservationList = new ArrayList<>();
                        reservationMap.put(quarter, reservationList); // Simpan list baru dalam map
                    }

                    // Tambahkan reservationResponseDTO ke dalam list
                    reservationList.add(reservationResponseDTO);
                }
            }
        });

        // Konversi Map menjadi List untuk respon
        var resultList = new ArrayList<Map<String, Object>>();

        if ("monthly".equalsIgnoreCase(period)) {
            for (int month = 1; month <= 12; month++) {
                var monthData = new HashMap<String, Object>();
                monthData.put("month", month);
                monthData.put("reservations", reservationMap.getOrDefault(month, new ArrayList<>()));
                resultList.add(monthData);
            }
        } else if ("quarterly".equalsIgnoreCase(period)) {
            for (int quarter = 1; quarter <= 4; quarter++) {
                var quarterData = new HashMap<String, Object>();
                quarterData.put("quarter", "Q" + quarter);
                quarterData.put("reservations", reservationMap.getOrDefault(quarter, new ArrayList<>()));
                resultList.add(quarterData);
            }
        }

        return resultList; 
    }



    private ReservationResponseDTO reservationToReservationResponseDTO(Reservation reservation) {
        ReservationResponseDTO reservationResponseDTO = new ReservationResponseDTO();

        // Set basic properties
        reservationResponseDTO.setId(reservation.getId());
        reservationResponseDTO.setDateIn(reservation.getDateIn());
        reservationResponseDTO.setDateOut(reservation.getDateOut());
        reservationResponseDTO.setTotalFee(reservation.getTotalFee());

        // Convert Patient to PatientResponseDTO
        Patient patient = reservation.getPatient();
        if (patient != null) {
            PatientResponseDTO patientResponseDTO = new PatientResponseDTO();
            patientResponseDTO.setId(patient.getId());
            patientResponseDTO.setNIK(patient.getNIK());
            patientResponseDTO.setName(patient.getName());
            patientResponseDTO.setEmail(patient.getEmail());
            patientResponseDTO.setBirthDate(patient.getBirthDate());
            patientResponseDTO.setGender(patient.isGender());
            reservationResponseDTO.setPatient(patientResponseDTO);
        }

        // Convert Room to RoomResponseDTO
        Room room = reservation.getRoom();
        if (room != null) {
            RoomResponseDTO roomResponseDTO = new RoomResponseDTO();
            roomResponseDTO.setId(room.getId());
            roomResponseDTO.setName(room.getName());
            roomResponseDTO.setDescription(room.getDescription());
            roomResponseDTO.setMaxCapacity(room.getMaxCapacity());
            roomResponseDTO.setPricePerDay(room.getPricePerDay());
            reservationResponseDTO.setRoom(roomResponseDTO);
        }

        // Convert Nurse to NurseResponseDTO
        Nurse nurse = reservation.getAssignedNurse();
        if (nurse != null) {
            NurseResponseDTO nurseResponseDTO = new NurseResponseDTO();
            nurseResponseDTO.setId(nurse.getId());
            nurseResponseDTO.setName(nurse.getName());
            nurseResponseDTO.setEmail(nurse.getEmail());
            nurseResponseDTO.setGender(nurse.isGender());
            reservationResponseDTO.setAssignedNurse(nurseResponseDTO);
        }

        // Convert Facilities to FacilityResponseDTO
        List<FacilityResponseDTO> listFacilityResponseDTO = new ArrayList<>();
        if (reservation.getFacilities() != null) {
            for (Facility facility : reservation.getFacilities()) {
                FacilityResponseDTO facilityResponseDTO = new FacilityResponseDTO();
                facilityResponseDTO.setId(facility.getId());
                facilityResponseDTO.setName(facility.getName());
                facilityResponseDTO.setFee(facility.getFee());
                listFacilityResponseDTO.add(facilityResponseDTO);
            }
        }
        reservationResponseDTO.setFacilities(listFacilityResponseDTO);

        // Set created and updated timestamps
        reservationResponseDTO.setCreatedAt(reservation.getCreatedAt());
        reservationResponseDTO.setUpdatedAt(reservation.getUpdatedAt());

        return reservationResponseDTO;
    }

}

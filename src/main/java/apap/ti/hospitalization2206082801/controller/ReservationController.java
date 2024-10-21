package apap.ti.hospitalization2206082801.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import apap.ti.hospitalization2206082801.dto.request.AddPatientRequestDTO;
import apap.ti.hospitalization2206082801.dto.request.AddReservationRequestDTO;
import apap.ti.hospitalization2206082801.dto.request.UpdateReservationRequestDTO;
import apap.ti.hospitalization2206082801.model.Facility;
import apap.ti.hospitalization2206082801.model.Patient;
import apap.ti.hospitalization2206082801.model.Reservation;
import apap.ti.hospitalization2206082801.model.Room;
import apap.ti.hospitalization2206082801.model.Nurse;
import apap.ti.hospitalization2206082801.service.FacilityService;
import apap.ti.hospitalization2206082801.service.NurseService;
import apap.ti.hospitalization2206082801.service.PatientService;
import apap.ti.hospitalization2206082801.service.ReservationService;
import apap.ti.hospitalization2206082801.service.RoomService;
import jakarta.validation.Valid;

@Controller
public class ReservationController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private NurseService nurseService;

    private final ReservationService reservationService;

    // Constructor Injection
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }
    
    enum StatusLevel {
        Ongoing,
        Done,
        Upcoming,
    }

    @GetMapping("/") 
    private String home(Model model) {
        int totalReservations = reservationService.getAllReservations().size();
        int totalRoom = roomService.getAllRooms().size();
        int totalPatient = patientService.getAllPatient().size();
        model.addAttribute("totalPatient", totalPatient);
        model.addAttribute("totalRoom", totalRoom);
        model.addAttribute("totalReservation", totalReservations);
        return "home";
    }

    @GetMapping("/reservations")
    public String listRoom(Model model) {
        List<Reservation> listReservations = reservationService.getAllReservations();
        Map<String, String> reservationStatusMap = new HashMap<>();
    
        for (Reservation reservation : listReservations) {
            String status = reservationStatus(reservation);
            reservationStatusMap.put(reservation.getId(), status);
        }

        model.addAttribute("listReservations", listReservations);
        model.addAttribute("reservationStatusMap", reservationStatusMap); 
        return "viewall-reservation";
    }

    public String reservationStatus(Reservation reservation) {
        Date now = new Date();
        if (reservation.getDateOut().before(now)) {
            return StatusLevel.Done.name();
        } else if (reservation.getDateIn().after(now)) {
            return StatusLevel.Upcoming.name();
        } else {
            return StatusLevel.Ongoing.name();
        }
    }

    @GetMapping("/reservations/create")
    public String createReservationHandler(
            @RequestParam(value = "step", required = false, defaultValue = "0") int step,
            @RequestParam(value = "nik", required = false, defaultValue = "") String nik,
            @RequestParam(value = "roomId", required = false) String roomId,
            Model model) {

        if (nik == null || nik.isEmpty() && step == 0){
            return "search-patient-reservations";
        }

        Patient patient = patientService.getPatientByNik(nik);
        if (patient == null && step == 0) {
            model.addAttribute("nik", nik);
            return "view-patient-not-found";
        }

        switch (step) {
            case 1:
                var patientDTO = new AddPatientRequestDTO();
                model.addAttribute("patientDTO", patientDTO);
                return "form-add-patient-step-1";
            case 2:
                var reservationDTO = new AddReservationRequestDTO();
                List<Nurse> nurse = nurseService.getAllNurse();
                List<Room> room = roomService.getAllRooms();

                model.addAttribute("nurse", nurse);
                model.addAttribute("room", room);
                model.addAttribute("patient", patient);
                model.addAttribute("reservationDTO", reservationDTO);
                return "form-add-reservation-step-2";
            default:
                if (patient != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    String bod = sdf.format(patient.getBirthDate());
                    model.addAttribute("bod", bod);
                }
                model.addAttribute("patient", patient);
                return "view-patient";
        }
    }


    @PostMapping("/reservations/create/patient")
    public String addPatient(
            @Valid @ModelAttribute AddPatientRequestDTO patientDTO,
            BindingResult result,
            Model model) {
        
        if (result.hasErrors()) {
            String errorMessage = result.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
            model.addAttribute("responseMessage", "Error: " + errorMessage);
            return "response-patient";
        }

        var patient = new Patient();
        patient.setNIK(patientDTO.getNIK());
        patient.setName(patientDTO.getName());
        patient.setEmail(patientDTO.getEmail());
        patient.setBirthDate(patientDTO.getBirthDate());
        patient.setGender(patientDTO.getGender());
        patient.setCreatedAt(new Date());
        patient.setUpdatedAt(new Date());
        patient.setDeletedAt(null);

        patientService.addPatient(patient);

        return "redirect:/reservations/create?nik=" + patient.getNIK() + "&step=2"; 
    }

    // Step 2 Create Reservation
    @PostMapping("/reservations/create")
    public String addReservation(
            @RequestParam String nik,
            @RequestParam String roomId,
            @Valid @ModelAttribute AddReservationRequestDTO reservationDTO,
            BindingResult result,
            Model model) {
        
        if (result.hasErrors()) {
            String errorMessage = result.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
            model.addAttribute("responseMessage", "Error: " + errorMessage);
            return "response-reservation"; // Ganti dengan halaman yang sesuai untuk kesalahan reservasi
        }

        // 1. Menghitung selisih tanggal
        long diffInMillies = reservationDTO.getDateOut().getTime() - reservationDTO.getDateIn().getTime();
        long diffInDays = diffInMillies / (24 * 60 * 60 * 1000);
        String dayDifference = String.format("%02d", diffInDays % 100); // Ambil 2 digit terakhir

        // 2. Ambil hari pembuatan reservasi (dari tanggal saat ini) menggunakan Date() 
        Date currentDate = new Date();
        String[] daysOfWeek = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        // Menggunakan Calendar untuk mendapatkan hari dari currentDate
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        String dayOfWeek = daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1]; // Hari dalam format SUN, MON, TUE, dll.

        // 3. 4 huruf terakhir dari NIK pasien
        String lastFourNIK = nik.length() > 4 ? nik.substring(nik.length() - 4) : nik; // Pastikan panjang NIK cukup

        // 4. Jumlah reservasi di sistem (termasuk yang sudah di-soft delete)
        int totalReservations = reservationService.getAllReservationsWithSoftDelete().size();
        String formattedCount = String.format("%04d", totalReservations); // Format menjadi 4 digit

        // 5. Membuat ID reservasi
        String reservationId = "RES" + dayDifference + dayOfWeek + lastFourNIK + formattedCount;



        var reservation = new AddReservationRequestDTO();

        Room room = roomService.getRoomById(reservationDTO.getRoomId());

        reservation.setTotalFee(room.getPricePerDay()*diffInDays);
        reservation.setAssignedNurseId(reservationDTO.getAssignedNurseId());
        reservation.setPatientId(reservationDTO.getPatientId());
        reservation.setRoomId(reservationDTO.getRoomId());
        reservation.setDateIn(reservationDTO.getDateIn());
        reservation.setDateOut(reservationDTO.getDateOut());

        Room selectedRoom = roomService.getRoomById(roomId);
        List<Facility> facilities = facilityService.getAllFacility();
        Patient patient = patientService.getPatientByNik(nik);
        int quota = roomService.getQuotaAvailable(roomId,reservationDTO.getDateIn(), reservationDTO.getDateOut());

        model.addAttribute("patient", patient);
        model.addAttribute("room", selectedRoom);
        model.addAttribute("listFacilities", facilities);
        model.addAttribute("reservationDTO", reservation);
        model.addAttribute("reservationId", reservationId);
        model.addAttribute("quota", quota);

        return "form-add-reservation-step-3";
    }

    @PostMapping("/reservations/create/facility")
    public String addFacility(
            @Valid @ModelAttribute AddReservationRequestDTO reservationDTO,
            @RequestParam String nik,
            @RequestParam String roomId,
            @RequestParam String reservationId,
            BindingResult result,
            Model model) {

        // Check for validation errors
        if (result.hasErrors()) {
            String errorMessage = result.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
            model.addAttribute("responseMessage", "Error: " + errorMessage);
            return "response-reservation"; // Redirect to a response page
        }

        // Create a new reservation object and set its properties
        Reservation reservation = new Reservation();
        reservation.setId(reservationId);
        reservation.setPatient(patientService.getPatientByNik(nik));
        reservation.setRoom(roomService.getRoomById(roomId));
        reservation.setDateIn(reservationDTO.getDateIn());
        reservation.setDateOut(reservationDTO.getDateOut());
        reservation.setAssignedNurse(nurseService.getNurseById(reservationDTO.getAssignedNurseId()));
        reservation.setPatient(patientService.getPatientById(reservationDTO.getPatientId()));

        // Calculate total fees based on selected facilities
        double totalFacilityFee = 0;
        List<Facility> facilities = new ArrayList<>();
        if(reservationDTO.getFacilities() != null) {
            for (Facility facility : reservationDTO.getFacilities()) {
                var facilityById = facilityService.getFacilityById(facility.getId());
                facilities.add(facilityById);
                totalFacilityFee += facilityById.getFee();
            }
        }
        reservation.setTotalFee(reservationDTO.getTotalFee() + totalFacilityFee);
        reservation.setCreatedAt(new Date());
        reservation.setUpdatedAt(new Date());

        // Add facilities to the reservation
        reservation.setFacilities(facilities);

        // Save the reservation
        reservationService.addReservation(reservation);
        model.addAttribute("responseMessage",
                String.format("Reservation dengan ID %s berhasil diupdate.", reservation.getId()));
        return "response-reservation"; 
    }

    // Additional method to handle adding a row for facilities
    @PostMapping(value = "/reservations/create/facility", params = {"addRow"})
    public String addRowFacilityReservation(
            @ModelAttribute AddReservationRequestDTO addReservationRequestDTO,
            @RequestParam String nik,
            @RequestParam String roomId,
            @RequestParam String reservationId,
            Model model) {

        // Initialize selected facility IDs if null
        if (addReservationRequestDTO.getFacilities() == null) {
            addReservationRequestDTO.setFacilities(new ArrayList<>());
        }

        // Add a new row for facility selection
        addReservationRequestDTO.getFacilities().add(null); // Add an empty entry to represent a new facility row

        // Prepare model attributes for rendering
        Room selectedRoom = roomService.getRoomById(roomId);
        List<Facility> facilities = facilityService.getAllFacility();
        Patient patient = patientService.getPatientByNik(nik);

        model.addAttribute("patient", patient);
        model.addAttribute("room", selectedRoom);
        model.addAttribute("listFacilities", facilities);
        model.addAttribute("reservationDTO", addReservationRequestDTO);
        model.addAttribute("reservationId", reservationId);

        return "form-add-reservation-step-3"; // Return to the form view
    }

    @PostMapping(value = "/reservations/create/facility", params = {"deleteRow"})
    public String deleteRowFacilityReservation(
            @ModelAttribute AddReservationRequestDTO addReservationRequestDTO,
            @RequestParam("deleteRow") int row,
            @RequestParam String nik,
            @RequestParam String roomId,
            @RequestParam String reservationId,
            Model model) {

        // Remove the specified row
        addReservationRequestDTO.getFacilities().remove(row);

        // Prepare model attributes for rendering
        Room selectedRoom = roomService.getRoomById(roomId);
        List<Facility> facilities = facilityService.getAllFacility();
        Patient patient = patientService.getPatientByNik(nik);

        model.addAttribute("patient", patient);
        model.addAttribute("room", selectedRoom);
        model.addAttribute("listFacilities", facilities);
        model.addAttribute("reservationDTO", addReservationRequestDTO);
        model.addAttribute("reservationId", reservationId);

        return "form-add-reservation-step-3"; // Return to the form view
    }

    @GetMapping("/reservations/{id}")
    public String detailReservations(@PathVariable("id") String id, Model model) {
        Reservation reservation = reservationService.getReservationById(id);
        Patient patient = reservation.getPatient();
        String nurse = reservation.getAssignedNurse().getName();
        List<Facility> facility = reservation.getFacilities();
        Room room = reservation.getRoom();
        String status = reservationStatus(reservation);
        long diffInMillies = reservation.getDateOut().getTime() - reservation.getDateIn().getTime();
        long diffInDays = diffInMillies / (24 * 60 * 60 * 1000);
        var totalPrice = room.getPricePerDay() * diffInDays;

        model.addAttribute("facility", facility);
        model.addAttribute("room", room);
        model.addAttribute("nurse",nurse);
        model.addAttribute("patient", patient);
        model.addAttribute("status", status);
        model.addAttribute("reservation", reservation);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalHari", diffInDays);
        return "view-reservation";
    }

    @PostMapping("/reservations/{reservationId}/delete")
    public String deleteReservations(@PathVariable("reservationId") String reservationId, Model model){
        var reservation = reservationService.getReservationById(reservationId);
        reservationService.deleteReservation(reservation);
        model.addAttribute("responseMessage",
                String.format("Berhasil menghapus reservasi %s", reservation.getId()));
        return "response-reservation";
    }

    @GetMapping("/reservations/{reservationId}/update-facilities")
    public String updateFacility(@PathVariable("reservationId") String reservationId, Model model) {
        var reservation = reservationService.getReservationById(reservationId);
        
        var reservationDTO = new UpdateReservationRequestDTO();

        reservationDTO.setId(reservation.getId());
        reservationDTO.setDateIn(reservation.getDateIn());
        reservationDTO.setDateOut(reservation.getDateOut());
        reservationDTO.setPatientId(reservation.getPatient().getId());
        reservationDTO.setAssignedNurseId(reservation.getAssignedNurse().getId());
        reservationDTO.setRoomId(reservation.getRoom().getId());
        reservationDTO.setTotalFee(reservation.getTotalFee());
        reservationDTO.setFacilities(reservation.getFacilities());
        
        List<Facility> facilities = facilityService.getAllFacility();

        model.addAttribute("listFacilities", facilities);
        model.addAttribute("reservationDTO", reservationDTO);
        return "form-update-reservation-facility";
    }

    @PostMapping("/reservations/{reservationId}/update-facilities")
    public String updateFacility(@PathVariable("reservationId") String reservationId, @Valid @ModelAttribute UpdateReservationRequestDTO reservationDTO, BindingResult result, Model model) {
        if(result.hasErrors()) {
            String errorMessage = result.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
            model.addAttribute("responseMessage", "Error: " + errorMessage);
            return "response-reservation";
        }
        
        var reservationFromDTO = new Reservation();
        reservationFromDTO.setId(reservationDTO.getId());
        reservationFromDTO.setDateIn(reservationDTO.getDateIn());
        reservationFromDTO.setDateOut(reservationDTO.getDateOut());
        reservationFromDTO.setPatient(patientService.getPatientById(reservationDTO.getPatientId()));
        reservationFromDTO.setAssignedNurse(nurseService.getNurseById(reservationDTO.getAssignedNurseId()));
        reservationFromDTO.setRoom(roomService.getRoomById(reservationDTO.getRoomId()));
        reservationFromDTO.setUpdatedAt(new Date());

        double totalFacilityFee = 0;
        List<Facility> facilities = new ArrayList<>();
        if(reservationDTO.getFacilities() != null) {
            for (Facility facility : reservationDTO.getFacilities()) {
                var facilityById = facilityService.getFacilityById(facility.getId());
                facilities.add(facilityById);
                totalFacilityFee += facilityById.getFee();
            }
        }
        long diffInMillies = reservationDTO.getDateOut().getTime() - reservationDTO.getDateIn().getTime();
        long diffInDays = diffInMillies / (24 * 60 * 60 * 1000);
        double feeRoom = roomService.getRoomById(reservationDTO.getRoomId()).getPricePerDay();
        reservationFromDTO.setTotalFee((feeRoom*diffInDays) + totalFacilityFee);
        reservationFromDTO.setFacilities(facilities);

        Date today = new Date();
        if (today.before(reservationDTO.getDateOut())) {
            // Lakukan pembaruan fasilitas
            reservationService.updateReservation(reservationFromDTO);
            return "redirect:/reservations/" + reservationId; // Redirect setelah berhasil
        } else {
            // Tanggal sudah lewat, tidak bisa melakukan update
            model.addAttribute("responseMessage",
                    String.format("Reservation dengan ID %s gagal diupdate karena tanggal tidak berlaku", reservationDTO.getId()));
            return "response-reservation";
        }
    }

    @PostMapping(value = "/reservations/{reservationId}/update-facilities", params = {"addRow"})
    public String updateAddRowFacilityReservation(
            @PathVariable("reservationId") String reservationId,
            @ModelAttribute UpdateReservationRequestDTO updateReservationRequestDTO,
            Model model) {

        // Initialize selected facility IDs if null
        if (updateReservationRequestDTO.getFacilities() == null) {
            updateReservationRequestDTO.setFacilities(new ArrayList<>());
        }

        // update a new row for facility selection
        updateReservationRequestDTO.getFacilities().add(null); // Add an empty entry to represent a new facility row

        List<Facility> facilities = facilityService.getAllFacility();

        model.addAttribute("listFacilities", facilities);
        model.addAttribute("reservationDTO", updateReservationRequestDTO);

        return "form-update-reservation-facility"; // Return to the form view
    }

    @PostMapping(value = "/reservations/{reservationId}/update-facilities", params = {"deleteRow"})
    public String updateDeleteRowFacilityReservation(
            @ModelAttribute UpdateReservationRequestDTO updateReservationRequestDTO,
            @RequestParam("deleteRow") int row,
            @PathVariable("reservationId") String reservationId,
            Model model) {

        // Remove the specified row
        updateReservationRequestDTO.getFacilities().remove(row);

        List<Facility> facilities = facilityService.getAllFacility();
        
        model.addAttribute("listFacilities", facilities);
        model.addAttribute("reservationDTO", updateReservationRequestDTO);

        return "form-update-reservation-facility"; // Return to the form view
    }

    @GetMapping("/reservations/{reservationId}/update-room")
    public String updateRoom(@PathVariable("reservationId") String reservationId, Model model) {
        var reservation = reservationService.getReservationById(reservationId);
        
        var reservationDTO = new UpdateReservationRequestDTO();

        reservationDTO.setId(reservation.getId());
        reservationDTO.setDateIn(reservation.getDateIn());
        reservationDTO.setDateOut(reservation.getDateOut());
        reservationDTO.setPatientId(reservation.getPatient().getId());
        reservationDTO.setAssignedNurseId(reservation.getAssignedNurse().getId());
        reservationDTO.setRoomId(reservation.getRoom().getId());
        reservationDTO.setTotalFee(reservation.getTotalFee());
        reservationDTO.setFacilities(reservation.getFacilities());
        
        List<Nurse> nurse = nurseService.getAllNurse();
        List<Room> room = roomService.getAllRooms();
        model.addAttribute("nurse", nurse);
        model.addAttribute("room", room);
        model.addAttribute("reservationDTO", reservationDTO);
        return "form-update-reservation-room";
    }

    @PostMapping("/reservations/{reservationId}/update-room")
    public String updateRoom(@PathVariable("reservationId") String reservationId, @Valid @ModelAttribute UpdateReservationRequestDTO reservationDTO, BindingResult result, Model model) {
        if(result.hasErrors()) {
            String errorMessage = result.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
            model.addAttribute("responseMessage", "Error: " + errorMessage);
            return "response-reservation";
        }
        
        var reservationFromDTO = new Reservation();
        reservationFromDTO.setId(reservationDTO.getId());
        reservationFromDTO.setDateIn(reservationDTO.getDateIn());
        reservationFromDTO.setDateOut(reservationDTO.getDateOut());
        reservationFromDTO.setPatient(patientService.getPatientById(reservationDTO.getPatientId()));
        reservationFromDTO.setAssignedNurse(nurseService.getNurseById(reservationDTO.getAssignedNurseId()));
        reservationFromDTO.setRoom(roomService.getRoomById(reservationDTO.getRoomId()));
        reservationFromDTO.setUpdatedAt(new Date());
        reservationFromDTO.setTotalFee(reservationDTO.getTotalFee());
        reservationFromDTO.setFacilities(reservationDTO.getFacilities());

        Date today = new Date();
        if (today.before(reservationDTO.getDateIn())) {
            // Lakukan pembaruan room
            reservationService.updateReservation(reservationFromDTO);
            return "redirect:/reservations/" + reservationId; // Redirect setelah berhasil
        } else {
            // Tanggal sudah lewat, tidak bisa melakukan update
            model.addAttribute("responseMessage",
                    String.format("Reservation dengan ID %s gagal diupdate karena tanggal tidak berlaku", reservationDTO.getId()));
            return "response-reservation";
        }
    }

    @GetMapping("/reservations/chart")
    public String getReservationChart(Model model) {
        List<Reservation> listReservations = reservationService.getAllReservations();
        Map<String, String> reservationStatusMap = new HashMap<>();
    
        for (Reservation reservation : listReservations) {
            String status = reservationStatus(reservation);
            reservationStatusMap.put(reservation.getId(), status);
        }

        model.addAttribute("listReservations", listReservations);
        model.addAttribute("reservationStatusMap", reservationStatusMap); 
        return "reservation-chart";
    }


}

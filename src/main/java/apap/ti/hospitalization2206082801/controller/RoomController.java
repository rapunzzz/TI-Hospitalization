package apap.ti.hospitalization2206082801.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import apap.ti.hospitalization2206082801.dto.request.AddRoomRequestDTO;
import apap.ti.hospitalization2206082801.dto.request.UpdateRoomRequestDTO;
import apap.ti.hospitalization2206082801.model.Reservation;
import apap.ti.hospitalization2206082801.model.Room;
import apap.ti.hospitalization2206082801.service.RoomService;
import jakarta.validation.Valid;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping("/rooms")
    public String listRoom(@RequestParam(value = "nama", required = false, defaultValue = "") String nama, 
                            @RequestParam(value = "status", required = false, defaultValue = "") String status, 
                            Model model) {
        List<Room> listRoom = roomService.getAllRooms();
        model.addAttribute("listRoom",listRoom);
        return "viewall-rooms";
    }

    @GetMapping("/rooms/create")
    public String addRoomsForm(Model model) {

        var roomDTO = new AddRoomRequestDTO();

        model.addAttribute("roomDTO", roomDTO);

        return "form-add-room";
    }
    @PostMapping("/rooms/create")
    public String addRooms(@Valid @ModelAttribute AddRoomRequestDTO roomDTO, BindingResult result, Model model) {
        if(result.hasErrors()) {
            String errorMessage = result.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
            model.addAttribute("responseMessage", "Error: " + errorMessage);
            return "response-room";
        }

        var room = new Room();
        int n = roomService.getAllRooms().size();
        String newRoomId = String.format("RM%04d", n+1);

        room.setId(newRoomId);
        room.setName(roomDTO.getName());
        room.setDescription(roomDTO.getDescription());
        room.setMaxCapacity(roomDTO.getMaxCapacity());
        room.setPricePerDay(roomDTO.getPricePerDay());
        room.setReservations(roomDTO.getReservations());
        room.setCreatedAt(new Date());
        room.setUpdatedAt(new Date());
        room.setDeletedAt(null);

        roomService.addRoom(room);

        model.addAttribute("responseMessage",
                String.format("Room %s dengan ID %s berhasil ditambahkan.", room.getName(), room.getId()));
        return "response-room";
    }

    @GetMapping("/rooms/{id}")
    public String detailRoom(@PathVariable("id") String id, Model model) {
        Date dateIn = new Date();
        Date dateOut = new Date();
        var room = roomService.getRoomById(id);
        int quotaAvailable = roomService.getQuotaAvailable(id, dateIn, dateOut);

        model.addAttribute("room", room);
        model.addAttribute("quota", quotaAvailable);
        return "view-room";
    }

    @PostMapping("/rooms/{roomId}/delete")
    public String deleteRoom(@PathVariable("roomId") String roomId, Model model){
        var room = roomService.getRoomById(roomId);
        roomService.deleteRoom(room);
        model.addAttribute("responseMessage",
                String.format("Room %s dengan ID %s berhasil dihapus.", room.getName(), room.getId()));
        return "response-room";
    }

    @GetMapping("/rooms/{id}/update")
    public String updateRoom(@PathVariable("id") String id, Model model) {
        var room = roomService.getRoomById(id);
   
        var roomDTO = new UpdateRoomRequestDTO();
        roomDTO.setId(room.getId());
        roomDTO.setName(room.getName());
        roomDTO.setDescription(room.getDescription());
        roomDTO.setMaxCapacity(room.getMaxCapacity());
        roomDTO.setPricePerDay(room.getPricePerDay());
        roomDTO.setUpdatedAt(room.getUpdatedAt());
        roomDTO.setReservations(room.getReservations());

        model.addAttribute("roomDTO", roomDTO);
        return "form-update-room";
    }

    @PostMapping("/rooms/{id}/update")
    public String updateRoom(@PathVariable("id") String id, @Valid @ModelAttribute UpdateRoomRequestDTO roomDTO, BindingResult result, Model model) {
        if(result.hasErrors()) {
            String errorMessage = result.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
            model.addAttribute("responseMessage", "Error: " + errorMessage);
            return "response-room";
        }
        
        var roomFromDTO = new Room();
        roomFromDTO.setId(roomDTO.getId());
        roomFromDTO.setName(roomDTO.getName());
        roomFromDTO.setDescription(roomDTO.getDescription());
        roomFromDTO.setMaxCapacity(roomDTO.getMaxCapacity());
        roomFromDTO.setPricePerDay(roomDTO.getPricePerDay());
        roomFromDTO.setReservations(roomDTO.getReservations());
        roomFromDTO.setUpdatedAt(roomDTO.getUpdatedAt());
        var room = roomService.updateRoom(roomFromDTO);

        model.addAttribute("responseMessage",
                String.format("Room %s dengan ID %s berhasil diupdate.", room.getName(), room.getId()));
        return "response-room";
    }


    @GetMapping("/rooms/search")
    public ResponseEntity<List<Room>> searchRooms(
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateIn,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOut) {
        List<Room> availableRooms = roomService.findAvailableRooms(dateIn, dateOut);
        return ResponseEntity.ok(availableRooms); // Return the list wrapped in a ResponseEntity
    }

   @GetMapping("/rooms/{roomId}/search")
    public ResponseEntity<Map<String, Object>> searchReservationByRoomsId(
        @PathVariable String roomId,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateIn,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOut) {

        List<Reservation> reservationInclude = roomService.getReservationsDetailRoomByDate(roomId, dateIn, dateOut);
        int quotaAvailable = roomService.getQuotaAvailable(roomId, dateIn, dateOut);

    
        Map<String, Object> response = new HashMap<>();
        response.put("reservationInclude", reservationInclude);
        response.put("quota", quotaAvailable);

        return ResponseEntity.ok(response);
    }
}

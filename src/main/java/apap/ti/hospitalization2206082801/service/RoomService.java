package apap.ti.hospitalization2206082801.service;

import java.util.Date;
import java.util.List;

import apap.ti.hospitalization2206082801.model.Reservation;
import apap.ti.hospitalization2206082801.model.Room;

public interface RoomService {
    Room addRoom(Room room);
    List<Room> getAllRooms();
    Room getRoomById(String id);
    void deleteRoom(Room room);
    Room updateRoom(Room room);
    List<Room> findAvailableRooms(Date dateIn, Date dateOut);
    int getQuotaAvailable(String roomId,Date dateIn, Date dateOut);
    List<Reservation> getReservationsDetailRoomByDate(String roomId, Date dateIn, Date dateOut);
}

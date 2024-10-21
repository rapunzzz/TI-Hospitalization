package apap.ti.hospitalization2206082801.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import apap.ti.hospitalization2206082801.model.Reservation;
import apap.ti.hospitalization2206082801.model.Room;
import apap.ti.hospitalization2206082801.repository.RoomDb;

@Service
public class RoomServiceImpl implements RoomService{
    @Autowired
    RoomDb roomDb;

    @Override
    public Room addRoom(Room room) {
        return roomDb.save(room);
    }

    @Override
    public List<Room> getAllRooms() {
        Sort.Order orderByRoomId = Sort.Order.by("id").ignoreCase(); // Urutkan berdasarkan roomId
        Sort.Order orderByNama = Sort.Order.by("name").ignoreCase(); // Urutkan berdasarkan nama
        Sort sort = Sort.by(orderByRoomId).and(Sort.by(orderByNama)); // Gabungkan kedua urutan
        return roomDb.findByDeletedAtIsNull(sort);
    }

    @Override
    public Room getRoomById(String idRoom) {
        for (Room room : getAllRooms()) {
            if (room.getId().equals(idRoom)) {
                return room;
            }
        }
        return null;
    }

    @Override
    public void deleteRoom(Room room) {
        room.setDeletedAt(new Date());
        roomDb.save(room);
    }

    @Override
    public Room updateRoom(Room room) {
        Room getRoom = getRoomById(room.getId());
        System.out.println(room.getId()+"dapet id apa");
        if (getRoom != null) {
            getRoom.setName(room.getName());
            getRoom.setDescription(room.getDescription());
            getRoom.setMaxCapacity(room.getMaxCapacity());
            getRoom.setPricePerDay(room.getPricePerDay());
            getRoom.setUpdatedAt(new Date());
            getRoom.setReservations(room.getReservations());
            roomDb.save(getRoom);
            System.out.println(getRoom.getId() + "tes dapet id");
            return getRoom;
        }
        System.out.println("gadapet id");
        return null;
    }

    @Override
    public List<Room> findAvailableRooms(Date dateIn, Date dateOut) {
        List<Room> allRooms = getAllRooms(); // Retrieve all rooms from the database
        List<Room> availableRooms = new ArrayList<>();

        for (Room room : allRooms) {
            int overlappingReservationsCount = 0; // Count of overlapping reservations

            // Check all reservations related to this room
            for (Reservation reservation : room.getReservations()) {
                Date existingDateIn = reservation.getDateIn();
                Date existingDateOut = reservation.getDateOut();

                // Check for date overlap conditions
                if ((dateIn.before(existingDateOut) && dateOut.after(existingDateIn)) || 
                    dateIn.equals(existingDateIn) || dateOut.equals(existingDateOut)) {
                    overlappingReservationsCount++; // Increase the count if dates overlap
                }
            }

            if (overlappingReservationsCount < room.getMaxCapacity()) {
                availableRooms.add(room); 
            }
        }

        return availableRooms; // Return the list of available rooms
    }

    @Override
    public List<Reservation> getReservationsDetailRoomByDate(String roomId, Date dateIn, Date dateOut) {
        Room room = getRoomById(roomId);
        List<Reservation> reservationList = room.getReservations();
        List<Reservation> reservationInclude = new ArrayList<>();

        for (Reservation reservation : reservationList) {
            Date existingDateIn = reservation.getDateIn();
            Date existingDateOut = reservation.getDateOut();

            // Check for date overlap conditions
            if ((dateIn.before(existingDateOut) && dateOut.after(existingDateIn)) || 
                dateIn.equals(existingDateIn) || dateOut.equals(existingDateOut)) {
                reservationInclude.add(reservation);
            }
        }

        return reservationInclude;
    }

    @Override
    public int getQuotaAvailable(String roomId,Date dateIn, Date dateOut){
        var room = getRoomById(roomId);
        List<Reservation> reservationList = room.getReservations();
        List<Reservation> reservationInclude = new ArrayList<>();
        int quotaAvailable = room.getMaxCapacity();

        for (Reservation reservation : reservationList) {
            Date existingDateIn = reservation.getDateIn();
            Date existingDateOut = reservation.getDateOut();

            // Check for date overlap conditions
            if ((dateIn.before(existingDateOut) && dateOut.after(existingDateIn)) || 
                dateIn.equals(existingDateIn) || dateOut.equals(existingDateOut)) {
                    reservationInclude.add(reservation);
                    quotaAvailable--; // Decrease the count if dates overlap
            }
        }

        return quotaAvailable;
    }
}

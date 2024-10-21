package apap.ti.hospitalization2206082801.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import apap.ti.hospitalization2206082801.model.Patient;
import apap.ti.hospitalization2206082801.model.Reservation;

@SpringBootTest
public class ReservationsServiceTests {

    @Mock
    private RoomService roomService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testFilterReservationsDetailRoomByDate() {
        String roomId = "RM0002";
        Date dateIn = new Date(2024 - 1900, 8, 1);  // Note: Month is 0-based
        Date dateOut = new Date(2024 - 1900, 8, 5);

        Patient patient1 = new Patient();
        patient1.setName("John Doe");

        Patient patient2 = new Patient();
        patient2.setName("Jane Smith");

        Reservation reservation1 = new Reservation();
        reservation1.setDateIn(new Date(2024 - 1900, 8, 2));
        reservation1.setDateOut(new Date(2024 - 1900, 8, 4));
        reservation1.setPatient(patient1);

        Reservation reservation2 = new Reservation();
        reservation2.setDateIn(new Date(2024 - 1900, 8, 4));
        reservation2.setDateOut(new Date(2024 - 1900, 8, 6));
        reservation2.setPatient(patient2);

        List<Reservation> expectedReservations = Arrays.asList(reservation1, reservation2);

        // Mock the behavior of getReservationsDetailRoomByDate
        when(roomService.getReservationsDetailRoomByDate(roomId, dateIn, dateOut))
            .thenReturn(expectedReservations);

        // Act
        List<Reservation> result = roomService.getReservationsDetailRoomByDate(roomId, dateIn, dateOut);

        // Assert
        assertEquals(2, result.size());
        for (Reservation reservation : result) {
            assertTrue(dateIn.before(reservation.getDateOut()) && dateOut.after(reservation.getDateIn()) || 
                dateIn.equals(reservation.getDateIn()) || dateOut.equals(reservation.getDateOut()));
        }
        assertEquals("John Doe", result.get(0).getPatient().getName());
        assertEquals("Jane Smith", result.get(1).getPatient().getName());

        // Verify that the method was called with correct parameters
        verify(roomService).getReservationsDetailRoomByDate(roomId, dateIn, dateOut);

        
    }
}

package apap.ti.hospitalization2206082801;

import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.Locale;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.github.javafaker.Faker;

import apap.ti.hospitalization2206082801.model.Facility;
import apap.ti.hospitalization2206082801.model.Nurse;
import apap.ti.hospitalization2206082801.model.Room;
import apap.ti.hospitalization2206082801.service.FacilityService;
import apap.ti.hospitalization2206082801.service.NurseService;
import apap.ti.hospitalization2206082801.service.RoomService;
import jakarta.transaction.Transactional;

@SpringBootApplication
public class Hospitalization2206082801Application {

	public static void main(String[] args) {
		SpringApplication.run(Hospitalization2206082801Application.class, args);
	}

	@Bean
	@Transactional
	CommandLineRunner run(RoomService roomService, NurseService nurseService, FacilityService facilityService) {
		return args -> {
			var faker = new Faker(new Locale("in-ID"));

			Room room = new Room();

			int roomCount = roomService.getAllRooms().size();
			String newRoomId = String.format("RM%04d", roomCount + 1); // Generate a unique room ID

			room.setId(newRoomId);
			room.setName(faker.leagueOfLegends().champion()); // Generate a fake name for the room
			room.setDescription(faker.lorem().sentence()); // Generate a fake description
			room.setMaxCapacity(faker.number().numberBetween(1, 10)); // Random max capacity
			room.setPricePerDay(faker.number().randomDouble(2, 500, 2000)); // Random price per day
			room.setCreatedAt(new Date());
			room.setUpdatedAt(new Date());
			room.setDeletedAt(null); // Make sure deleted_at is null for active rooms

			// Save the room to the database
			roomService.addRoom(room);

			Nurse nurse = new Nurse();
            nurse.setName(faker.name().fullName()); // Generate a random name
            nurse.setEmail(faker.internet().emailAddress()); // Generate a random email
            nurse.setGender(faker.bool().bool()); // Randomly assign gender (true/false)
            nurse.setCreatedAt(new Date());
            nurse.setUpdatedAt(new Date());
            nurse.setDeletedAt(null); // Set deleted_at to null for active nurses

            nurseService.addNurse(nurse);



			List<String> hospitalFacilities = Arrays.asList(
				"Private Room",
				"TV",
				"WiFi",
				"Meals (Dietary Service)",
				"Nurse Call System",
				"Oxygen Supply",
				"Wheelchair Accessibility",
				"Medical Monitoring Equipment",
				"Bed Linens and Towels",
				"Shower Facility",
				"Visitor Chair",
				"Pharmacy Service",
				"Emergency Button",
				"Physical Therapy Equipment",
				"Recliner for Attendant"
			);
			if(facilityService.getAllFacility().isEmpty()) {
				for (int i = 0; i < 15; i++) { // Generate 10 facilities
					Facility facility = new Facility();
					String facilityName = hospitalFacilities.get(i); // Randomly select a facility name
					facility.setName(facilityName); // Set the facility name
					facility.setFee(faker.number().randomDouble(2, 100, 1000)); // Generate a random fee between 100 and 1000
					facility.setCreatedAt(new Date()); // Set the creation date to the current date
					facility.setUpdatedAt(new Date()); // Set the updated date to the current date
					facility.setDeletedAt(null); // Set deletedAt to null if the facility hasn't been deleted
	
					facilityService.addFacility(facility);
				}
			}
		};
	}

}

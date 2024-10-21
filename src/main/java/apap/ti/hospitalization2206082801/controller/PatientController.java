package apap.ti.hospitalization2206082801.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import apap.ti.hospitalization2206082801.dto.request.AddPatientRequestDTO;
import apap.ti.hospitalization2206082801.model.Patient;
import apap.ti.hospitalization2206082801.service.PatientService;
import jakarta.validation.Valid;

import java.util.Date;
import java.util.stream.Collectors;


@Controller
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping("/patient/create")
    public String addPatientForm(Model model) {

        var patientDTO = new AddPatientRequestDTO();

        model.addAttribute("patientDTO", patientDTO);

        return "form-add-patient-step-1";
    }
    @PostMapping("/patient/create")
    public String addPatient(@Valid @ModelAttribute AddPatientRequestDTO patientDTO, BindingResult result, Model model) {
        if(result.hasErrors()) {
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

        return "form-add-reservation-step-2";
    }
}

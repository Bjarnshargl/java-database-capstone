package com.project.back_end.services;


import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

// @Service // <- IntelliJ doesn't like it...
@org.springframework.stereotype.Service // <- IntelliJ's suggestion
public class Service {
    // 1. **@Service Annotation**
    // The @Service annotation marks this class as a service component in Spring. This allows Spring to automatically detect it through component scanning
    // and manage its lifecycle, enabling it to be injected into controllers or other services using @Autowired or constructor injection.
    // Done

    // 2. **Constructor Injection for Dependencies**
    // The constructor injects all required dependencies (TokenService, Repositories, and other Services). This approach promotes loose coupling, improves testability,
    // and ensures that all required dependencies are provided at object creation time.

    // Services:
    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public Service(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   DoctorService doctorService,
                   PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    // 3. **validateToken Method**
    // This method checks if the provided JWT token is valid for a specific user. It uses the TokenService to perform the validation.
    // If the token is invalid or expired, it returns a 401 Unauthorized response with an appropriate error message. This ensures security by preventing
    // unauthorized access to protected resources.
    public ResponseEntity<Map<String, String>> validateToken (String token, String user){
        Map<String, String> response = new HashMap<>();
        if (tokenService.validateToken(token, user)){
            response.put("response", "The token is valid");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("response", "Sorry, the token is not valid.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 4. **validateAdmin Method**
    // This method validates the login credentials for an admin user.
    // - It first searches the admin repository using the provided username.
    // - If an admin is found, it checks if the password matches.
    // - If the password is correct, it generates and returns a JWT token (using the admin’s username) with a 200 OK status.
    // - If the password is incorrect, it returns a 401 Unauthorized status with an error message.
    // - If no admin is found, it also returns a 401 Unauthorized.
    // - If any unexpected error occurs during the process, a 500 Internal Server Error response is returned.
    // This method ensures that only valid admin users can access secured parts of the system.
    ResponseEntity<Map<String, String>> validateAdmin (Admin receivedAdmin){
        Admin foundAdmin = adminRepository.findByUsername(receivedAdmin.getUsername());
        Map<String, String> response = new HashMap<>();
        if (foundAdmin != null){
            if (foundAdmin.getPassword().equals(receivedAdmin.getPassword())){
                tokenService.generateToken(foundAdmin.getUsername());
                response.put("response", "The admin credentials are valid, a token was created.");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put("response", "Sorry, admin credentials were invalid.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } else {
            response.put("response", "Sorry, admin was not found.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 5. **filterDoctor Method**
    // This method provides filtering functionality for doctors based on name, specialty, and available time slots.
    // - It supports various combinations of the three filters.
    // - If none of the filters are provided, it returns all available doctors.
    // This flexible filtering mechanism allows the frontend or consumers of the API to search and narrow down doctors based on user criteria.
    public Map<String, Object> filterDoctor(String name, String speciality, String time){
        // an empty list is okay, so i check nothing:
        return doctorService.filterDoctorsByNameSpecilityandTime(name, speciality, time);
    }

    // 6. **validateAppointment Method**
    // This method validates if the requested appointment time for a doctor is available.
    // - It first checks if the doctor exists in the repository.
    // - Then, it retrieves the list of available time slots for the doctor on the specified date.
    // - It compares the requested appointment time with the start times of these slots.
    // - If a match is found, it returns 1 (valid appointment time).
    // - If no matching time slot is found, it returns 0 (invalid).
    // - If the doctor doesn’t exist, it returns -1.
    // This logic prevents overlapping or invalid appointment bookings.
    int validateAppointment (Appointment appointment){
        Doctor doctor = appointment.getDoctor();
        if (doctor == null){
            // The doctor doesn't exist
            return -1;
        }

        List<String> doctors = doctorService.getDoctorAvailability(doctor.getId(), appointment.getAppointmentTime().toLocalDate());
        if (doctors.isEmpty()){
            // The doctor is unavailable
            return 0;
        } else {
            // The doctor is available
            return 1;
        }
    }

    // 7. **validatePatient Method**
    // This method checks whether a patient with the same email or phone number already exists in the system.
    // - If a match is found, it returns false (indicating the patient is not valid for new registration).
    // - If no match is found, it returns true.
    // This helps enforce uniqueness constraints on patient records and prevent duplicate entries.
    boolean validatePatient (Patient patient){
        Patient patientFound = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());
        return patientFound != null;
    }

    // 8. **validatePatientLogin Method**
    // This method handles login validation for patient users.
    // - It looks up the patient by email.
    // - If found, it checks whether the provided password matches the stored one.
    // - On successful validation, it generates a JWT token and returns it with a 200 OK status.
    // - If the password is incorrect or the patient doesn't exist, it returns a 401 Unauthorized with a relevant error.
    // - If an exception occurs, it returns a 500 Internal Server Error.
    // This method ensures only legitimate patients can log in and access their data securely.
    ResponseEntity<Map<String, String>> validatePatientLogin (Login login){
        Patient patient = patientRepository.findByEmail(login.getEmail());
        Map<String, String> response = new HashMap<>();
        if (patient != null){
            if (patient.getPassword().equals(login.getPassword())){
                tokenService.generateToken(patient.getEmail());
                response.put("response", "The patient credentials are valid, a token was created.");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put("response", "Sorry, patient credentials were invalid.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } else {
            response.put("response", "Sorry, patient was not found.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 9. **filterPatient Method**
    // This method filters a patient's appointment history based on condition and doctor name.
    // - It extracts the email from the JWT token to identify the patient.
    // - Depending on which filters (condition, doctor name) are provided, it delegates the filtering logic to PatientService.
    // - If no filters are provided, it retrieves all appointments for the patient.
    // This flexible method supports patient-specific querying and enhances user experience on the client side.
    ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        Map<String, Object> filteredList = new HashMap<>();
        List<Appointment> combinedAppointments = new ArrayList<>();
        ResponseEntity<Map<String, Object>> response1 = null;
        ResponseEntity<Map<String, Object>> response2 = null;
        ResponseEntity<Map<String, Object>> response3 = null;

        try {
            String patientEmail = tokenService.extractIdentifier(token);
            Patient patient = patientRepository.findByEmail(patientEmail);

            if (patient == null) {
                filteredList.put("error", "Patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(filteredList);
            }

            Long patientId = patient.getId();

            response1 = patientService.filterByCondition(condition, patientId);
            response2 = patientService.filterByDoctor(name, patientId);
            response3 = patientService.filterByDoctorAndCondition(condition, name, patientId);

            if (response1 != null && response1.getBody() != null) {
                List<Appointment> list1 = (List<Appointment>) response1.getBody().get("appointments");
                if (list1 != null) combinedAppointments.addAll(list1);
            }
            if (response2 != null && response2.getBody() != null) {
                List<Appointment> list2 = (List<Appointment>) response2.getBody().get("appointments");
                if (list2 != null) combinedAppointments.addAll(list2);
            }
            if (response3 != null && response3.getBody() != null) {
                List<Appointment> list3 = (List<Appointment>) response3.getBody().get("appointments");
                if (list3 != null) combinedAppointments.addAll(list3);
            }

            filteredList.put("appointments", combinedAppointments);
            return ResponseEntity.ok(filteredList);

        } catch (Exception e) {
            filteredList.put("error", "Internal error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(filteredList);
        }
    }


}

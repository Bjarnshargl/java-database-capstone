package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PatientService {
    // 1. **Add @Service Annotation**:
    //    - The `@Service` annotation is used to mark this class as a Spring service component.
    //    - It will be managed by Spring's container and used for business logic related to patients and appointments.
    //    - Instruction: Ensure that the `@Service` annotation is applied above the class declaration.
    // Done

    // 2. **Constructor Injection for Dependencies**:
    //    - The `PatientService` class has dependencies on `PatientRepository`, `AppointmentRepository`, and `TokenService`.
    //    - These dependencies are injected via the constructor to maintain good practices of dependency injection and testing.
    //    - Instruction: Ensure constructor injection is used for all the required dependencies.
    PatientRepository patientRepository;
    AppointmentRepository appointmentRepository;
    TokenService tokenService;

    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 3. **createPatient Method**:
    //    - Creates a new patient in the database. It saves the patient object using the `PatientRepository`.
    //    - If the patient is successfully saved, the method returns `1`; otherwise, it logs the error and returns `0`.
    //    - Instruction: Ensure that error handling is done properly and exceptions are caught and logged appropriately.
    int createPatient(Patient patient){
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // 4. **getPatientAppointment Method**:
    //    - Retrieves a list of appointments for a specific patient, based on their ID.
    //    - The appointments are then converted into `AppointmentDTO` objects for easier consumption by the API client.
    //    - This method is marked as `@Transactional` to ensure database consistency during the transaction.
    //    - Instruction: Ensure that appointment data is properly converted into DTOs and the method handles errors gracefully.
    ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token){
        List<Appointment> appointments = appointmentRepository.findByPatientId(id);
        Map<String, Object> appointmentsMap = new HashMap<>();

        try {
            // AppointmentDTO(
            // Long id,
            // Long doctorId,
            // String doctorName,
            // Long patientId,
            // String patientName,
            // String patientEmail,
            // String patientPhone,
            // String patientAddress,
            // LocalDateTime appointmentTime,
            // int status)
            List<AppointmentDTO> appointmentsDTO = appointments.stream().map(appointment -> new AppointmentDTO(
                    appointment.getId(),
                    appointment.getDoctor().getId(),
                    appointment.getDoctor().getName(),
                    appointment.getPatient().getId(),
                    appointment.getPatient().getName(),
                    appointment.getPatient().getEmail(),
                    appointment.getPatient().getPhone(),
                    appointment.getPatient().getAddress(),
                    appointment.getAppointmentTime(),
                    appointment.getStatus()
            )).toList();

            appointmentsMap.put("appointments", appointmentsDTO);
            return ResponseEntity.status(HttpStatus.OK).body(appointmentsMap);
        } catch (Exception e) {
            appointmentsMap.put("error", "unknown error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(appointmentsMap);
        }
    }

    // 5. **filterByCondition Method**:
    //    - Filters appointments for a patient based on the condition (e.g., "past" or "future").
    //    - Retrieves appointments with a specific status (0 for future, 1 for past) for the patient.
    //    - Converts the appointments into `AppointmentDTO` and returns them in the response.
    //    - Instruction: Ensure the method correctly handles "past" and "future" conditions, and that invalid conditions are caught and returned as errors.
    ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id){
        // The method checks the condition value (past or future) and filters appointments accordingly.
        // It uses the status (1 for past and 0 for future) to determine the filtering criteria.
        // condition:
        //    'status' field:
        //    - Type: private int
        //    - Description:
        //      - Represents the current status of the appointment. It is an integer where:
        //        - 0 means the appointment is scheduled.
        //        - 1 means the appointment has been completed.
        //      - The @NotNull annotation ensures that the status field is not null.
        List<Appointment> appointments = new ArrayList<>();
        Map<String, Object> appointmentsMap = new HashMap<>();

        if (condition.equalsIgnoreCase("past")){
            appointments = appointmentRepository.filterByDoctorNameAndPatientIdAndStatus("", id, 1);
        } else if (condition.equalsIgnoreCase("future")) {
            appointments = appointmentRepository.filterByDoctorNameAndPatientIdAndStatus("", id, 0);
        } else {
            // return an empty list:
            appointmentsMap.put("error", appointments);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(appointmentsMap);
        }

        try {
            List<AppointmentDTO> appointmentsDTO = appointments.stream().map(appointment -> new AppointmentDTO(
                    appointment.getId(),
                    appointment.getDoctor().getId(),
                    appointment.getDoctor().getName(),
                    appointment.getPatient().getId(),
                    appointment.getPatient().getName(),
                    appointment.getPatient().getEmail(),
                    appointment.getPatient().getPhone(),
                    appointment.getPatient().getAddress(),
                    appointment.getAppointmentTime(),
                    appointment.getStatus()
            )).toList();

            appointmentsMap.put("appointments", appointmentsDTO);
            return ResponseEntity.status(HttpStatus.OK).body(appointmentsMap);
        } catch (Exception e) {
            appointmentsMap.put("error", "unknown error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(appointmentsMap);
        }
    }


    // 6. **filterByDoctor Method**:
    //    - Filters appointments for a patient based on the doctor's name.
    //    - It retrieves appointments where the doctor’s name matches the given value, and the patient ID matches the provided ID.
    //    - Instruction: Ensure that the method correctly filters by doctor's name and patient ID and handles any errors or invalid cases.
    ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId){
        List<Appointment> appointments = new ArrayList<>();
        Map<String, Object> appointmentsMap = new HashMap<>();

        try {
            appointments = appointmentRepository.filterByDoctorNameAndPatientId(name, patientId);

            List<AppointmentDTO> appointmentsDTO = appointments.stream().map(appointment -> new AppointmentDTO(
                    appointment.getId(),
                    appointment.getDoctor().getId(),
                    appointment.getDoctor().getName(),
                    appointment.getPatient().getId(),
                    appointment.getPatient().getName(),
                    appointment.getPatient().getEmail(),
                    appointment.getPatient().getPhone(),
                    appointment.getPatient().getAddress(),
                    appointment.getAppointmentTime(),
                    appointment.getStatus()
            )).toList();

            appointmentsMap.put("appointments", appointmentsDTO);
            return ResponseEntity.status(HttpStatus.OK).body(appointmentsMap);
        } catch (Exception e) {
            appointmentsMap.put("error", "unknown error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(appointmentsMap);
        }
    }

    // 7. **filterByDoctorAndCondition Method**:
    //    - Filters appointments based on both the doctor's name and the condition (past or future) for a specific patient.
    //    - This method combines filtering by doctor name and appointment status (past or future).
    //    - Converts the appointments into `AppointmentDTO` objects and returns them in the response.
    //    - Instruction: Ensure that the filter handles both doctor name and condition properly, and catches errors for invalid input.
    ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        List<Appointment> appointments = new ArrayList<>();
        Map<String, Object> appointmentsMap = new HashMap<>();
        try {
            int status = -1;
            if (condition.equalsIgnoreCase("past")){
                status = 1;
            }
            if (condition.equalsIgnoreCase("future")) {
                status = 0;
            }
            appointments = appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(name, patientId, status);

            List<AppointmentDTO> appointmentsDTO = appointments.stream().map(appointment -> new AppointmentDTO(
                    appointment.getId(),
                    appointment.getDoctor().getId(),
                    appointment.getDoctor().getName(),
                    appointment.getPatient().getId(),
                    appointment.getPatient().getName(),
                    appointment.getPatient().getEmail(),
                    appointment.getPatient().getPhone(),
                    appointment.getPatient().getAddress(),
                    appointment.getAppointmentTime(),
                    appointment.getStatus()
            )).toList();

            appointmentsMap.put("appointments", appointmentsDTO);
            return ResponseEntity.status(HttpStatus.OK).body(appointmentsMap);

        } catch (Exception e) {
            appointmentsMap.put("error", "unknown error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(appointmentsMap);
        }
    }

    // 8. **getPatientDetails Method**:
    //    - Retrieves patient details using the `tokenService` to extract the patient's email from the provided token.
    //    - Once the email is extracted, it fetches the corresponding patient from the `patientRepository`.
    //    - It returns the patient's information in the response body.
    //    - Instruction: Make sure that the token extraction process works correctly and patient details are fetched properly based on the extracted email.
    ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        List<Appointment> appointments = new ArrayList<>();
        Map<String, Object> appointmentsMap = new HashMap<>();

        try {
            String patientEmail = tokenService.extractIdentifier(token);
            Patient patient = patientRepository.findByEmail(patientEmail);
            Long patientId = patient.getId();
            appointments = appointmentRepository.findByPatientId(patientId);

            List<AppointmentDTO> appointmentsDTO = appointments.stream().map(appointment -> new AppointmentDTO(
                    appointment.getId(),
                    appointment.getDoctor().getId(),
                    appointment.getDoctor().getName(),
                    appointment.getPatient().getId(),
                    appointment.getPatient().getName(),
                    appointment.getPatient().getEmail(),
                    appointment.getPatient().getPhone(),
                    appointment.getPatient().getAddress(),
                    appointment.getAppointmentTime(),
                    appointment.getStatus()
            )).toList();

            appointmentsMap.put("appointments", appointmentsDTO);
            return ResponseEntity.status(HttpStatus.OK).body(appointmentsMap);
        } catch (Exception e) {
            appointmentsMap.put("error", "unknown error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(appointmentsMap);
        }
    }

    // 9. **Handling Exceptions and Errors**:
    //    - The service methods handle exceptions using try-catch blocks and log any issues that occur. If an error occurs during database operations, the service responds with appropriate HTTP status codes (e.g., `500 Internal Server Error`).
    //    - Instruction: Ensure that error handling is consistent across the service, with proper logging and meaningful error messages returned to the client.

    // 10. **Use of DTOs (Data Transfer Objects)**:
    //    - The service uses `AppointmentDTO` to transfer appointment-related data between layers. This ensures that sensitive or unnecessary data (e.g., password or private patient information) is not exposed in the response.
    //    - Instruction: Ensure that DTOs are used appropriately to limit the exposure of internal data and only send the relevant fields to the client.



}

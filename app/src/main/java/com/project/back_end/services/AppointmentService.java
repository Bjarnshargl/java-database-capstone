package com.project.back_end.services;

// dependencies like `AppointmentRepository`, `Service`, `TokenService`, `PatientRepository`, and `DoctorRepository`
// Repositories:
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// Services:
// import com.project.back_end.services.Service;
// import com.project.back_end.services.TokenService;


@Service
public class AppointmentService {
    // 1. **Add @Service Annotation**:
    //    - To indicate that this class is a service layer class for handling business logic.
    //    - The `@Service` annotation should be added before the class declaration to mark it as a Spring service component.
    //    - Instruction: Add `@Service` above the class definition.

    // 2. **Constructor Injection for Dependencies**:
    //    - The `AppointmentService` class requires several dependencies like `AppointmentRepository`, `Service`, `TokenService`, `PatientRepository`, and `DoctorRepository`.
    //    - These dependencies should be injected through the constructor.
    //    - Instruction: Ensure constructor injection is used for proper dependency management in Spring.
    private AppointmentRepository appointmentRepository;
    private PatientRepository patientRepository;
    private DoctorRepository doctorRepository;
    private Service service;
    private TokenService tokenService;

    @Autowired
    AppointmentService (AppointmentRepository appointmentRepository,
                        Service service,
                        TokenService tokenService,
                        PatientRepository patientRepository,
                        DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this. doctorRepository = doctorRepository;
    }

    // 3. **Add @Transactional Annotation for Methods that Modify Database**:
    //    - The methods that modify or update the database should be annotated with `@Transactional` to ensure atomicity and consistency of the operations.
    //    - Instruction: Add the `@Transactional` annotation above methods that interact with the database, especially those modifying data.

    // 4. **Book Appointment Method**:
    //    - Responsible for saving the new appointment to the database.
    //    - If the save operation fails, it returns `0`; otherwise, it returns `1`.
    //    - Instruction: Ensure that the method handles any exceptions and returns an appropriate result code.
    private int bookAppointment (Appointment appointment){
        // Assignment:
        //  the content does not include a method that retrieves appointments
        //  by doctor and date, which is necessary for the second criterion
        try {
            appointmentRepository.save(appointment);
            return 1; // successful
        } catch (Exception e) {
            return 0; // failed
            //throw new RuntimeException(e);
        }
    }

    // 5. **Update Appointment Method**:
    //    - This method is used to update an existing appointment based on its ID.
    //    - It validates whether the patient ID matches, checks if the appointment is available for updating, and ensures that the doctor is available at the specified time.
    //    - If the update is successful, it saves the appointment; otherwise, it returns an appropriate error message.
    //    - Instruction: Ensure proper validation and error handling is included for appointment updates.
    private ResponseEntity<Map<String, String>> updateAppointment (Appointment appointment){
        Map<String, String> message = new HashMap<>();
        if (appointmentRepository.findById(appointment.getId()).isPresent() &&
                service.validateAppointment(appointment) == 1){

            LocalDateTime start = appointment.getAppointmentTime();
            LocalDateTime end = appointment.getAppointmentTime().plusHours(1);
            Long doctorId = appointment.getDoctor().getId();
            List<Appointment> conflictingAppointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);

            if (conflictingAppointments.isEmpty()){
                appointmentRepository.save(appointment);
                message.put("message", "Update successful!");
                return ResponseEntity.status(HttpStatus.OK).body(message);
            } else {
                message.put("message", "Sorry, there's an appointment time conflict.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
        }
        if (appointmentRepository.findById(appointment.getId()).isEmpty()){
            message.put("message", "Sorry, this appointment does not exist.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        if (service.validateAppointment(appointment) == 0){
            message.put("message", "Sorry, this appointment contains invalid data.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        else {
            message.put("message", "Sorry, this update cannot be proceeded.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }

    }

    // 6. **Cancel Appointment Method**:
    //    - This method cancels an appointment by deleting it from the database.
    //    - It ensures the patient who owns the appointment is trying to cancel it and handles possible errors.
    //    - Instruction: Make sure that the method checks for the patient ID match before deleting the appointment.
    ResponseEntity<Map<String, String>> cancelAppointment (long id, String token){
        Map<String, String> message = new HashMap<>();
        if (appointmentRepository.findById(id).isPresent()){
            try {
                String user = tokenService.extractEmail(token);
                boolean valid = tokenService.validateToken(token, user);
                Appointment appointment = appointmentRepository.getById(id);
                if (valid){
                    //appointmentRepository.updateStatus(1, id); // 1 means "completed", but there's no "canceled".
                    appointmentRepository.delete(appointment);
                    message.put("message", "Update successful!");
                    return ResponseEntity.status(HttpStatus.OK).body(message);
                } else {
                    message.put("message", "Sorry, you are not permitted to change this status.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
                }
            } catch (Exception e) {
                message.put("message", "Sorry, something went wrong.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
                throw new RuntimeException(e);
            }
        } else {
            message.put("message", "Sorry, something went wrong.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }

    // 7. **Get Appointments Method**:
    //    - This method retrieves a list of appointments for a specific doctor on a particular day, optionally filtered by the patient's name.
    //    - It uses `@Transactional` to ensure that database operations are consistent and handled in a single transaction.
    //    - Instruction: Ensure the correct use of transaction boundaries, especially when querying the database for appointments.
    Map<String, Object> getAppointment (String pname, LocalDate date, String token){
        Map<String, Object> appointmentsFound = new HashMap<>();

        // Get all the needed doctor data:
        String doctorEmail = tokenService.extractEmail(token);
        Doctor doctor = doctorRepository.findByEmail(doctorEmail);
        Long doctorId = doctor.getId();

        // Build start and end times:
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusHours(24);

        List<Appointment> appointments = new ArrayList<>();

        if (pname.isEmpty()){
            appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
        } else {
            appointments = appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(doctorId, pname, start, end);
        }

        appointmentsFound.put("appointments", appointments);
        return appointmentsFound;
    }

    ArrayList<Appointment> getAppointmentsByDoctorAndDate(Long doctorId, LocalDate date) {
        ArrayList<Appointment> appointments = new ArrayList<>();
        // Logik zum Füllen der Liste
        return appointments;
    }

    // 8. **Change Status Method**:
    //    - This method updates the status of an appointment by changing its value in the database.
    //    - It should be annotated with `@Transactional` to ensure the operation is executed in a single transaction.
    //    - Instruction: Add `@Transactional` before this method to ensure atomicity when updating appointment status.



}

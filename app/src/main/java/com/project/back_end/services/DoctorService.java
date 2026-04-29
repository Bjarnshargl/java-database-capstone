package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

// 1. **Add @Service Annotation**:
//    - This class should be annotated with `@Service` to indicate that it is a service layer class.
//    - The `@Service` annotation marks this class as a Spring-managed bean for business logic.
//    - Instruction: Add `@Service` above the class declaration.

@Service
public class DoctorService {
    // 2. **Constructor Injection for Dependencies**:
    //    - The `DoctorService` class depends on `DoctorRepository`, `AppointmentRepository`, and `TokenService`.
    //    - These dependencies should be injected via the constructor for proper dependency management.
    //    - Instruction: Ensure constructor injection is used for injecting dependencies into the service.
    private AppointmentRepository appointmentRepository;
    private PatientRepository patientRepository;
    private DoctorRepository doctorRepository;
    private Service service;
    private TokenService tokenService;
    private AppointmentService appointmentService;

    @Autowired
    public DoctorService(AppointmentRepository appointmentRepository,
                         PatientRepository patientRepository,
                         DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    // 3. **Add @Transactional Annotation for Methods that Modify or Fetch Database Data**:
    //    - Methods like `getDoctorAvailability`, `getDoctors`, `findDoctorByName`, `filterDoctorsBy*` should be annotated with `@Transactional`.
    //    - The `@Transactional` annotation ensures that database operations are consistent and wrapped in a single transaction.
    //    - Instruction: Add the `@Transactional` annotation above the methods that perform database operations or queries.

    // 4. **getDoctorAvailability Method**:
    //    - Retrieves the available time slots for a specific doctor on a particular date and filters out already booked slots.
    //    - The method fetches all appointments for the doctor on the given date and calculates the availability by comparing against booked slots.
    //    - Instruction: Ensure that the time slots are properly formatted and the available slots are correctly filtered.
    List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        // Find doctor by ID
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            // Doctor not found: return empty list
            return new ArrayList<>();
        }

        // Write available time slots to a modifiable list
        List<String> availabilities = new ArrayList<>(doctorOpt.get().getAvailableTimes());

        // Define the time range for the given date (from start to end of the day)
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusHours(24);

        // Get all appointments for the doctor within the time range
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);

        // Create a formatter for time slots (e.g., "09:00-10:00")
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        // Map each appointment's start time to its corresponding time slot string
        // I hope each appointment is exactly one hour long...
        // "Each time slot is represented as a string (e.g., "09:00-10:00", "10:00-11:00")."
        List<String> booked = appointments.stream()
                .map(appointment -> {
                    LocalDateTime t = appointment.getAppointmentTime();
                    return t.format(formatter) + "-" + t.plusHours(1).format(formatter);
                })
                .toList();

        // 7. Remove all booked slots from the list of available slots
        availabilities.removeIf(booked::contains);

        // 8. Return the list of available time slots
        return availabilities;
    }

    // 5. **saveDoctor Method**:
    //    - Used to save a new doctor record in the database after checking if a doctor with the same email already exists.
    //    - If a doctor with the same email is found, it returns `-1` to indicate conflict; `1` for success, and `0` for internal errors.
    //    - Instruction: Ensure that the method correctly handles conflicts and exceptions when saving a doctor.
    int saveDoctor (Doctor doctor){
        // Find doctor by ID
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctor.getId());
        // A doctor can be saved if it doesn't already exist:
        if (doctorOpt.isEmpty()) {
            try {
                doctorRepository.save(doctor);
                return 1;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return 0;
        }
    }

    // 6. **updateDoctor Method**:
    //    - Updates an existing doctor's details in the database. If the doctor doesn't exist, it returns `-1`.
    //    - Instruction: Make sure that the doctor exists before attempting to save the updated record and handle any errors properly.
    int updateDoctor (Doctor doctor){
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctor.getId());
        // A doctor can only be updated when he exists:
        if (doctorOpt.isPresent()) {
            return 0;
        } else {
            try {
                doctorRepository.save(doctor);
                return 1;
            } catch (Exception e) {
                return -1;
            }
        }
    }

    // 7. **getDoctors Method**:
    //    - Fetches all doctors from the database. It is marked with `@Transactional` to ensure that the collection is properly loaded.
    //    - Instruction: Ensure that the collection is eagerly loaded, especially if dealing with lazy-loaded relationships (e.g., available times).
    List<Doctor> getDoctors (){
        List<Doctor> allDoctors = new ArrayList<>();
        try {
            // returns all doctors that were found:
            allDoctors = doctorRepository.findAll();
            return allDoctors;
        } catch (Exception e) {
            // returns an empty list if nothing was found:
            return allDoctors;
        }
    }

    // 8. **deleteDoctor Method**:
    //    - Deletes a doctor from the system along with all appointments associated with that doctor.
    //    - It first checks if the doctor exists. If not, it returns `-1`; otherwise, it deletes the doctor and their appointments.
    //    - Instruction: Ensure the doctor and their appointments are deleted properly, with error handling for internal issues.
    int deleteDoctor (long id){
        Optional<Doctor> doctorOpt = doctorRepository.findById(id);
        if (doctorOpt.isEmpty()) {
            // nothing found that could be deleted:
            return 0;
        } else {
            try {
                // Delete the doctor
                appointmentRepository.deleteAllByDoctorId(id);
                return 1;
            } catch (Exception e) {
                return -1;
            }
        }
    }

    // 9. **validateDoctor Method**:
    //    - Validates a doctor's login by checking if the email and password match an existing doctor record.
    //    - It generates a token for the doctor if the login is successful, otherwise returns an error message.
    //    - Instruction: Make sure to handle invalid login attempts and password mismatches properly with error responses.
    ResponseEntity<Map<String, String>> validateDoctor(Login login){
        Map<String, String> message = new HashMap<>();
        try {
            // find the right doctor:
            Doctor doctor = doctorRepository.findByEmail(login.getEmail());
            // check login credentials:
            if (doctor.getPassword().equals(login.getPassword())){
                // The doctor is fully validated:
                message.put("message", "Email and password are correct.");
                return ResponseEntity.status(HttpStatus.OK).body(message);
            }
        } catch (Exception e) {
            // The password is wrong because the email was already validated:
            message.put("message", "Sorry, the password is not correct.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        message.put("message", "Sorry, password or email are incorrect.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    // 10. **findDoctorByName Method**:
    //    - Finds doctors based on partial name matching and returns the list of doctors with their available times.
    //    - This method is annotated with `@Transactional` to ensure that the database query and data retrieval are properly managed within a transaction.
    //    - Instruction: Ensure that available times are eagerly loaded for the doctors.
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = new ArrayList<>();
        try {
            doctors = doctorRepository.findByNameLike(name);
            // Doctor not found:
            if (doctors.isEmpty()) {
                response.put(name, doctors);
                return response;
            } else {
                // Doctor was found:
                response.put(name, doctors);
                return response;
            }
        } catch (Exception e) {
            // Something else went wrong:
            response.put(name, doctors);
            return response;
        }
    }


    // 11. **filterDoctorsByNameSpecilityandTime Method**:
    //    - Filters doctors based on their name, specialty, and availability during a specific time (AM/PM).
    //    - The method fetches doctors matching the name and specialty criteria, then filters them based on their availability during the specified time period.
    //    - Instruction: Ensure proper filtering based on both the name and specialty as well as the specified time period.
    Map<String, Object> filterDoctorsByNameSpecilityandTime (String name, String specialty, String amOrPm){
        // String amOrPm is time of day: AM/PM
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = new ArrayList<>();
        List<Doctor> doctorsFiltered = new ArrayList<>();

        try {
            doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
            if (doctors.isEmpty()){
                response.put(name, new ArrayList<>());
                return response;
            }  else {
                // filterDoctorByTime
                doctorsFiltered = filterDoctorByTime(doctors, amOrPm);
                response.put(name, doctorsFiltered);
                return response;
            }
        } catch (Exception e) {
            response.put(name, new ArrayList<>());
            return response;
        }
    }


    // 12. **filterDoctorByTime Method**:
    //    - Filters a list of doctors based on whether their available times match the specified time period (AM/PM).
    //    - This method processes a list of doctors and their available times to return those that fit the time criteria.
    //    - Instruction: Ensure that the time filtering logic correctly handles both AM and PM time slots and edge cases.
    List<Doctor> filterDoctorByTime (List<Doctor> doctors, String amOrPm) {
        List<Doctor> doctorsFiltered = new ArrayList<>();
        for (Doctor doctor : doctors){
            List<String> availableTimes = doctor.getAvailableTimes();
            // Each time slot is represented as a string (e.g., "09:00-10:00", "10:00-11:00").
            // in any am case the start time is lower than 12 o clock:
            for (String available : availableTimes){
                if (amOrPm.equalsIgnoreCase("am")){
                    int startHour = Integer.parseInt(available.substring(0,1));
                    if (startHour < 12){
                        doctorsFiltered.add(doctor);
                    }
                }
            }
            // in any pm case the start time is higher than 12 o clock:
            for (String available : availableTimes){
                if (amOrPm.equalsIgnoreCase("pm")){
                    int startHour = Integer.parseInt(available.substring(0,1));
                    if (startHour > 12){
                        doctorsFiltered.add(doctor);
                    }
                }
            }
        }
        return doctorsFiltered;
    }

    // 13. **filterDoctorByNameAndTime Method**:
    //    - Filters doctors based on their name and the specified time period (AM/PM).
    //    - Fetches doctors based on partial name matching and filters the results to include only those available during the specified time period.
    //    - Instruction: Ensure that the method correctly filters doctors based on the given name and time of day (AM/PM).
    Map<String, Object> filterDoctorByNameAndTime (String name, String amOrPm){
        List<Doctor> doctors = new ArrayList<>();
        List<Doctor> doctorsFiltered = new ArrayList<>();
        Map<String, Object> response = new HashMap<>();

        try {
            doctors = doctorRepository.findAll();

            // An empty list will be returned if there isn't any doctor:
            if (doctors.isEmpty()){
                response.put(name, doctors);
                return response;

            // If doctors were found, they are filtered two times:
            } else {
                List<Doctor> doctorsFiltered1 = filterDoctorByName(name, doctors);
                List<Doctor> doctorsFiltered2 = filterDoctorByTime(doctorsFiltered1, amOrPm);
                response.put(name, doctorsFiltered2);
                return response;
            }
        } catch (Exception e) {
            response.put(name, doctors);
            return response;
        }
    }

    // 14. **filterDoctorByNameAndSpecility Method**:
    //    - Filters doctors by name and specialty.
    //    - It ensures that the resulting list of doctors matches both the name (case-insensitive) and the specified specialty.
    //    - Instruction: Ensure that both name and specialty are considered when filtering doctors.
    Map<String, Object> filterDoctorByNameAndSpecility (String name, String speciality){
        List<Doctor> doctors = new ArrayList<>();
        List<Doctor> doctorsFiltered = new ArrayList<>();
        Map<String, Object> response = new HashMap<>();

        try {
            doctors = doctorRepository.findAll();

            // An empty list will be returned if there isn't any doctor:
            if (doctors.isEmpty()){
                response.put(name, doctors);
                return response;

                // If doctors were found, they are filtered two times:
            } else {
                List<Doctor> doctorsFiltered1 = filterDoctorByName(name, doctors);
                List<Doctor> doctorsFiltered2 = filterDoctorBySpeciality(speciality, doctorsFiltered1);
                response.put(name, doctorsFiltered2);
                return response;
            }
        } catch (Exception e) {
            response.put(name, doctors);
            return response;
        }
    }


    // 15. **filterDoctorByTimeAndSpecility Method**:
    //    - Filters doctors based on their specialty and availability during a specific time period (AM/PM).
    //    - Fetches doctors based on the specified specialty and filters them based on their available time slots for AM/PM.
    //    - Instruction: Ensure the time filtering is accurately applied based on the given specialty and time period (AM/PM).
    Map<String, Object> filterDoctorByTimeAndSpecility (String amOrPm, String speciality){
        List<Doctor> doctors = new ArrayList<>();
        List<Doctor> doctorsFiltered = new ArrayList<>();
        Map<String, Object> response = new HashMap<>();

        try {
            doctors = doctorRepository.findAll();

            // An empty list will be returned if there isn't any doctor:
            if (doctors.isEmpty()){
                response.put(speciality, doctors);
                return response;

                // If doctors were found, they are filtered two times:
            } else {
                List<Doctor> doctorsFiltered1 = filterDoctorByTime(doctors, amOrPm);
                List<Doctor> doctorsFiltered2 = filterDoctorBySpeciality(speciality, doctorsFiltered1);
                response.put(speciality, doctorsFiltered2);
                return response;
            }
        } catch (Exception e) {
            response.put(speciality, doctors);
            return response;
        }
    }

    // 16. **filterDoctorBySpecility Method**:
    //    - Filters doctors based on their specialty.
    //    - This method fetches all doctors matching the specified specialty and returns them.
    //    - Instruction: Make sure the filtering logic works for case-insensitive specialty matching.
    List<Doctor> filterDoctorBySpeciality (String speciality, List<Doctor> doctors){
        List<Doctor> doctorsFiltered = new ArrayList<>();

        for (Doctor doctor : doctors){
            if (doctor.getSpecialty().equalsIgnoreCase(speciality)){
                doctorsFiltered.add(doctor);
            }
        }
        return doctorsFiltered;
    }

    // 17. **filterDoctorsByTime Method**:
    //    - Filters all doctors based on their availability during a specific time period (AM/PM).
    //    - The method checks all doctors' available times and returns those available during the specified time period.
    //    - Instruction: Ensure proper filtering logic to handle AM/PM time periods.
    List<Doctor> filterDoctorsByTime (List<Doctor> doctors, String amOrPm) {
        List<Doctor> doctorsFiltered = new ArrayList<>();
        for (Doctor doctor : doctors){
            List<String> availableTimes = doctor.getAvailableTimes();
            // Each time slot is represented as a string (e.g., "09:00-10:00", "10:00-11:00").
            // in any am case the start time is lower than 12 o clock:
            for (String available : availableTimes){
                if (amOrPm.equalsIgnoreCase("am")){
                    int startHour = Integer.parseInt(available.substring(0,1));
                    if (startHour < 12){
                        doctorsFiltered.add(doctor);
                    }
                }
            }
            // in any pm case the start time is higher than 12 o clock:
            for (String available : availableTimes){
                if (amOrPm.equalsIgnoreCase("pm")){
                    int startHour = Integer.parseInt(available.substring(0,1));
                    if (startHour > 12){
                        doctorsFiltered.add(doctor);
                    }
                }
            }
        }
        return doctorsFiltered;
    }

    /**
     * This is a useful helper function, which I used in many other functions
     * @param name doctor's name
     * @param doctors List of doctors
     * @return filtered list of doctors
     */
    List<Doctor> filterDoctorByName (String name, List<Doctor> doctors){
        List<Doctor> doctorsFiltered = new ArrayList<>();

        for (Doctor doctor : doctors){
            if (doctor.getName().equalsIgnoreCase(name)){
                doctorsFiltered.add(doctor);
            }
        }
        return doctorsFiltered;
    }


}

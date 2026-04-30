package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.back_end.services.Service;
import com.project.back_end.services.DoctorService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for doctor-related functionalities.
 * <p>
 * Provides endpoints for:
 * <ul>
 *   <li>Querying doctor availability</li>
 *   <li>Retrieving, adding, updating, and deleting doctors</li>
 *   <li>Doctor login</li>
 *   <li>Filtering doctors by name, time, and specialty</li>
 * </ul>
 * The controller uses token-based authorization and delegates business logic to DoctorService and Service.
 * All endpoints are prefixed with a configurable API path.
 */
@RestController
@RequestMapping("${api.path}" + "doctor")
public class DoctorController {

    // 1. Set Up the Controller Class:
    //    - Annotate the class with `@RestController` to define it as a REST controller that serves JSON responses.
    //    - Use `@RequestMapping("${api.path}doctor")` to prefix all endpoints with a configurable API path followed by "doctor".
    //    - This class manages doctor-related functionalities such as registration, login, updates, and availability.
    // Done

    // 2. Autowire Dependencies:
    //    - Inject `DoctorService` for handling the core logic related to doctors (e.g., CRUD operations, authentication).
    //    - Inject the shared `Service` class for general-purpose features like token validation and filtering.
    // Done
    Service service;
    DoctorService doctorService;

    /**
     * Define the `getDoctorAvailability` Method:
     *   - Handles HTTP GET requests to check a specific doctor’s availability on a given date.
     *   - Requires `user` type, `doctorId`, `date`, and `token` as path variables.
     *   - First validates the token against the user type.
     *   - If the token is invalid, returns an error response; otherwise, returns the availability status for the doctor.
     */
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<Long, List>> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorID,
            @PathVariable LocalDate date,
            @PathVariable String token
    ) {
        ResponseEntity<Map<String, String>> tokenResponse = service.validateToken(token, user);

        if (!tokenResponse.getStatusCode().equals(HttpStatus.OK)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new HashMap<>());
        }

        Map<Long, List> availabilities = new HashMap<>();
        try {
            availabilities.put(doctorID, doctorService.getDoctorAvailability(doctorID, date));
            return ResponseEntity.status(HttpStatus.OK).body(availabilities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new HashMap<>());
        }
    }

    /**
     * 4. Define the `getDoctor` Method:
     *    - Handles HTTP GET requests to retrieve a list of all doctors.
     *    - Returns the list within a response map under the key `"doctors"` with HTTP 200 OK status.
     */
    @GetMapping
    public ResponseEntity<Map<String, List>>getDoctor(){
        Map<String, List> response = new HashMap<>();
        try {
            List<Doctor> doctors = doctorService.getDoctors();
            response.put("doctors", doctors);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Define the `saveDoctor` Method:
     * - Handles HTTP POST requests to register a new doctor.
     * - Accepts a validated `Doctor` object in the request body and a token for authorization.
     * - Validates the token for the `"admin"` role before proceeding.
     * - If the doctor already exists, returns a conflict response; otherwise, adds the doctor and returns a success message.
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> saveDoctor(
            @PathVariable String token,
            Doctor doctor
    ){
        Map<String, String> response = null;
        try {
            ResponseEntity<Map<String, String>> tokenResponse = service.validateToken(token, "admin");

            if (!tokenResponse.getStatusCode().equals(HttpStatus.OK)) {
                response.put("Authorization", "Authorization failed");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            } else {
                try {
                    // Only add a doctor that not already is in the database:
                    if (doctorService.findDoctorByName(doctor.getName()).isEmpty()){
                        doctorService.saveDoctor(doctor);
                        response.put("Success", "Doctor added to db");
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    } else {
                        response.put("Conflict", "Doctor already exists");
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    }
                } catch (Exception e) {
                    response.put("Internal error", "Some internal error occurred");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            }
        } catch (Exception e) {
            response.put("Internal error", "Some internal error occurred\"");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Define the `doctorLogin` Method:
     * - Handles HTTP POST requests for doctor login.
     * - Accepts a validated `Login` DTO containing credentials.
     * - Delegates authentication to the `DoctorService` and returns login status and token information.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin (Login login){
        Map<String, String> response = null;
        ResponseEntity<Map<String, String>> validationResponse = doctorService.validateDoctor(login);
        try {
            doctorService.validateDoctor(login);

            if (!validationResponse.getStatusCode().equals(HttpStatus.OK)) {
                return validationResponse;
            } else {
                return validationResponse;
            }
        } catch (Exception e) {
            return validationResponse;
        }
    }

    /**
     * Define the `updateDoctor` Method:
     * - Handles HTTP PUT requests to update an existing doctor's information.
     * - Accepts a validated `Doctor` object and a token for authorization.
     * - Token must belong to an `"admin"`.
     * - If the doctor exists, updates the record and returns success; otherwise, returns not found or error messages.
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor (
            @PathVariable String token,
            Doctor doctor
    ){
        Map<String, String> response = null;

        try {
            ResponseEntity<Map<String, String>> tokenResponse = service.validateToken(token, "admin");

            if (!tokenResponse.getStatusCode().equals(HttpStatus.OK)) {
                response.put("Authorization", "Authorization failed");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            } else {
                doctorService.updateDoctor(doctor);
                response.put("Success", "Doctor updated");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        } catch (Exception e) {
            response.put("Internal error", "Some internal error occurred");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Define the `deleteDoctor` Method:
     * - Handles HTTP DELETE requests to remove a doctor by ID.
     * - Requires both doctor ID and an admin token as path variables.
     * - If the doctor exists, deletes the record and returns a success message; otherwise, responds with a not found or error message.
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor (
            @PathVariable Long id,
            @PathVariable String token
    ){
        Map<String, String> response = null;

        try {
            ResponseEntity<Map<String, String>> tokenResponse = service.validateToken(token, "admin");

            if (!tokenResponse.getStatusCode().equals(HttpStatus.OK)) {
                response.put("Authorization", "Authorization failed");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            } else {
                doctorService.deleteDoctor(id);
                response.put("Success", "Doctor deleted successfully");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        } catch (Exception e) {
            response.put("Internal error", "Some internal error occurred");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Define the `filter` Method:
     * - Handles HTTP GET requests to filter doctors based on name, time, and specialty.
     * - Accepts `name`, `time`, and `speciality` as path variables.
     * - Calls the shared `Service` to perform filtering logic and returns matching doctors in the response.
     */
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filter (
            @PathVariable String name,
            @PathVariable String time,
            String speciality
    ){
        Map<String, Object> doctors = new HashMap<>();
        try {
            doctors = service.filterDoctor(name, time, speciality);
            return ResponseEntity.status(HttpStatus.OK).body(doctors);
        } catch (Exception e) {
            // returning an empty list:
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(doctors);
        }
    }

}

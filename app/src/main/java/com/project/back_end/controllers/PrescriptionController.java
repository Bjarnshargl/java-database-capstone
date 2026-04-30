package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for managing prescriptions related to appointments.
 * Handles HTTP requests for creating and retrieving prescriptions.
 * Uses token-based authentication and role validation for secure access.
 * Depends on PrescriptionService for business logic, Service for token validation,
 * and AppointmentService for updating appointment status.
 * The base path for endpoints is configured via "${api.path}prescription".
 * Errors are handled within the methods or can be managed globally using @RestControllerAdvice.
 */
@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {
    
    // 1. Set Up the Controller Class:
    //    - Annotate the class with `@RestController` to define it as a REST API controller.
    //    - Use `@RequestMapping("${api.path}prescription")` to set the base path for all prescription-related endpoints.
    //    - This controller manages creating and retrieving prescriptions tied to appointments.
    // Done

    // 2. Autowire Dependencies:
    //    - Inject `PrescriptionService` to handle logic related to saving and fetching prescriptions.
    //    - Inject the shared `Service` class for token validation and role-based access control.
    //    - Inject `AppointmentService` to update appointment status after a prescription is issued.
    PrescriptionService prescriptionService;
    Service service;
    AppointmentService appointmentService;

    public PrescriptionController(PrescriptionService prescriptionService,
                                  Service service,
                                  AppointmentService appointmentService) {
        this.prescriptionService = prescriptionService;
        this.service = service;
        this.appointmentService = appointmentService;
    }

    /**
     * Define the `savePrescription` Method:
     * - Handles HTTP POST requests to save a new prescription for a given appointment.
     * - Accepts a validated `Prescription` object in the request body and a doctor’s token as a path variable.
     * - Validates the token for the `"doctor"` role.
     * - If the token is valid, updates the status of the corresponding appointment to reflect that a prescription has been added.
     * - Delegates the saving logic to `PrescriptionService` and returns a response indicating success or failure.
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription (
            @PathVariable String token,
            Prescription prescription
    ){
        Map<String, String> response = null;
        try {
            ResponseEntity<Map<String, String>> tokenResponse = service.validateToken(token, "admin");

            if (!tokenResponse.getStatusCode().equals(HttpStatus.OK)) {
                response.put("Authorization", "Authorization failed");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            } else {
                prescriptionService.savePrescription(prescription);
                response.put("Success", "Prescription saved");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        } catch (Exception e) {
            response.put("Internal error", "Some internal error occurred");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Define the `getPrescription` Method:
     * - Handles HTTP GET requests to retrieve a prescription by its associated appointment ID.
     * - Accepts the appointment ID and a doctor’s token as path variables.
     * - Validates the token for the `"doctor"` role using the shared service.
     * - If the token is valid, fetches the prescription using the `PrescriptionService`.
     * - Returns the prescription details or an appropriate error message if validation fails.
     */
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, String>> getPrescription (
            @PathVariable Long appointmentId,
            @PathVariable String token
    ){
        Map<String, String> response = null;
        try {
            ResponseEntity<Map<String, String>> tokenResponse = service.validateToken(token, "admin");

            if (!tokenResponse.getStatusCode().equals(HttpStatus.OK)) {
                response.put("Authorization", "Authorization failed");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            } else {
                ResponseEntity<Map<String, Object>> prescription =  prescriptionService.getPrescription(appointmentId);
                if (prescription.getStatusCode().equals(HttpStatus.OK)){
                    response.put("Success", "Prescription loaded");
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                } else {
                    response.put("Message", "No prescription exists for that appointment found.");
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }
            }
        } catch (Exception e) {
            response.put("Internal error", "Some internal error occurred");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

}

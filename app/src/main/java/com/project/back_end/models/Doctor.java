package com.project.back_end.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * The Doctor class represents a medical doctor entity in the system and is mapped to a database table via JPA.
 * It stores essential information such as name, specialty, email, password, phone number, and available time slots.
 * Field validations ensure data integrity, and standard getter and setter methods provide access to the attributes.
 */
@Entity
public class Doctor {

    /**
     * The unique identifier for each doctor.
     * Automatically generated as the primary key in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the doctor.
     * Must be between 3 and 100 characters.
     */
    @NotNull
    @Size(min = 3, max = 100)
    private String name;

    /**
     * The medical specialty of the doctor.
     * Must be between 3 and 100 characters.
     */
    @NotNull
    @Size(min = 3, max = 100)
    private String specialty;

    /**
     * The email address of the doctor.
     * Must be a valid email format.
     */
    @NotNull
    @Email
    private String email;

    /**
     * The password for doctor authentication.
     * Must be at least 6 characters.
     * Only writable, not readable in serialized responses.
     */
    @NotNull
    @Size(min = 6)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * The phone number of the doctor.
     * Must be exactly 10 digits.
     */
    @NotNull
    @Pattern(regexp = "^[0-9]{10}$")
    private String phone;

    /**
     * The list of available time slots for the doctor.
     * Each time slot is represented as a string (e.g., "09:00-10:00").
     */
    @ElementCollection
    private List<String> availableTimes;

    /**
     * Constructs a new Doctor with the specified id, name, specialty, email, password, phone number, and available times.
     *
     * @param id             the unique identifier of the doctor
     * @param name           the name of the doctor
     * @param specialty      the medical specialty of the doctor
     * @param email          the email address of the doctor
     * @param password       the password for authentication
     * @param phone          the phone number of the doctor
     * @param availableTimes the list of available time slots for the doctor
     */
    public Doctor(Long id, String name, String specialty, String email, String password, String phone, List<String> availableTimes) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.availableTimes = availableTimes;
    }

    /**
     * Returns the ID of the doctor.
     * @return the doctor's ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the doctor.
     * @param id the doctor's ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the name of the doctor.
     * @return the doctor's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the doctor.
     * @param name the doctor's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the medical specialty of the doctor.
     * @return the doctor's specialty
     */
    public String getSpecialty() {
        return specialty;
    }

    /**
     * Sets the medical specialty of the doctor.
     * @param specialty the doctor's specialty
     */
    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    /**
     * Returns the email address of the doctor.
     * @return the doctor's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the doctor.
     * @param email the doctor's email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the password of the doctor.
     * @return the doctor's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the doctor.
     * @param password the doctor's password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the phone number of the doctor.
     * @return the doctor's phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number of the doctor.
     * @param phone the doctor's phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns the list of available time slots for the doctor.
     * @return the doctor's available times
     */
    public List<String> getAvailableTimes() {
        return availableTimes;
    }

    /**
     * Sets the list of available time slots for the doctor.
     * @param availableTimes the doctor's available times
     */
    public void setAvailableTimes(List<String> availableTimes) {
        this.availableTimes = availableTimes;
    }
}
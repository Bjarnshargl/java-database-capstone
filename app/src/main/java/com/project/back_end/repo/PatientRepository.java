package com.project.back_end.repo;

import com.project.back_end.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing Patient entities.
 * Provides methods to retrieve Patients by various identifiers.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Retrieves a Patient by their email address.
     * @param email the email address of the Patient
     * @return the Patient object if found, or null if not found
     */
    Patient findByEmail(String email);

    /**
     * Retrieves a Patient by either their email address or phone number.
     * This method can return a Patient based on either identifier.
     * Note: If both identifiers match, the first found Patient will be returned.
     * @param email the email address of the Patient
     * @param phone the phone number of the Patient
     * @return the Patient object if found, or null if not found
     */
    Patient findByEmailOrPhone(String email, String phone);

}


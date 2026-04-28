package com.project.back_end.repo;

import com.project.back_end.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Retrieves a Patient by their email address
    Patient findByEmail(String email);

    // Retrieves a Patient by either their email address or phone number
    Patient findByEmailOrPhone(String email, String phone);

}


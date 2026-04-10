# Architecture summary

This Spring Boot application uses both MVC and REST controllers. Thymeleaf templates are used for the Admin and Doctor dashboards, while REST APIs serve all other modules. The application interacts with two databases—MySQL (for patient, doctor, appointment, and admin data) and MongoDB (for prescriptions). All controllers route requests through a common service layer, which in turn delegates to the appropriate repositories. MySQL uses JPA entities while MongoDB uses document models.

---

## Numbered flow of data and control
1. User accesses AdminDashboard or Appointment pages.
2. The action is routed to the appropriate Thymeleaf or REST controller.
3. The controller calls the service layer.
4. The database repositories (MySQL and MongoDB) are required for data access.
5. A MySQL database for relational data and a MongoDB for NoSQL data are connected.
6. Models for MySQL and MongoDB are defined.
7. Data models:  
   7.1 The MySQL models are Patient, Doctor, and Appointment.  
   7.2 The MongoDB model is called Prescription.

## Components in Detail

### Dashboards

- **AdminDashboard:** For administrators to manage the application.
- **DoctorDashboard:** For doctors to manage patients and appointments.

### REST Modules

- **Appointments:** Managing appointments.
- **PatientDashboard:** For patients to view and manage their information.
- **PatientRecord:** Managing and displaying patient data.

### Spring Boot Application

- **Thymeleaf Controllers:** For server-side rendering and interaction with Thymeleaf templates.
- **REST Controllers:** For REST endpoints and API interactions.
- **Service Layer:** Contains the business logic of the application.

### Data Access

#### MySQL Repositories (using Spring Data JPA)

- **Patient:** Patient information.
- **DoctorDashboard:** Doctor dashboard data.
- **Appointment:** Appointments.
- **Admin:** Administration data.

#### MongoDB Repository

- **Prescription:** Prescriptions.

---  

## Summary

The application is built using Spring Boot and features multiple dashboards and REST modules. Structured data is stored in a MySQL database, while prescription documents are stored in MongoDB.  
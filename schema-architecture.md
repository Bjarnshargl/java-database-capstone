# Architecture summary

In this Spring Boot application, MVC and REST controllers are used. Thymeleaf is employed for displaying dashboards in the frontend and is available to the user groups "Admin" and "Doctor". Two databases are used: one relational (MySQL) and one non-relational (MongoDB). Patient, doctor, appointment, and admin data are relational, whereas prescriptions are managed by MongoDB. MySQL uses JPA models, while MongoDB uses document models. The service layer plays a central role, directing all requests to the appropriate repositories.

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
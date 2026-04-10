# Tasks
## Define a MySQL database design section:
Include at least 4 tables (e.g., patients, doctors, appointments, and admin)
For each table, specify column names, data types, primary and foreign keys, and constraints (e.g., NOT NULL, UNIQUE)

Define a MongoDB collection design section:

Choose a suitable document collection (e.g., prescriptions, feedback, and logs)

Provide a realistic JSON example of a document with nested fields or arrays.

Justify your design decisions using inline comments if needed.

Commit and push your file to GitHub.

## MySQL Database Design

### Table: patients
- id: INT, Primary Key, Auto Increment
- name: VARCHAR(255)
- email: VARCHAR(255)
- password: VARCHAR(255)
- phone: VARCHAR(255)
- address: VARCHAR(255)

### Table: doctors
- id: INT, Primary Key, Auto Increment
- name: VARCHAR(255), Not Null
- specialty: VARCHAR(50)
- email: VARCHAR(255), UNIQUE, Not Null
- password: VARCHAR(25), Not Null
- phone: VARCHAR(10)
- availableTimes: INT, Foreign Key → availableTimes(id)

### Table: available_times
- id: INT, Primary Key, Auto Increment
- timeslot: VARCHAR(255), Not Null

### Table: appointments
- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key → doctors(id)
- patient_id: INT, Foreign Key → patients(id)
- appointment_time: DATETIME, Not Null
- status: INT (0 = Scheduled, 1 = Completed, 2 = Cancelled)
  (Comment: A stored procedure can later delete all appointments that are "Cancelled" and which appointment_time is older more x days in the past)

### Table: admin
- id: INT, Primary Key, Auto Increment
- username: VARCHAR(25), Not Null
- password: VARCHAR(25), Not Null

## MongoDB Collection Design

### Collection: feedback
```json
{
  "_id": "ObjectId('63abc123456')",
  "doctorId": "192cd3838034",
  "appointmentId": "ap123456789",
  "feedback": {
    "stars": 5,
    "message": "Very friendly :-)",
    "tags": ["friendly", "helpful"]
  },
  "category": "Doctor Evaluation",
  "submittedAt": "2024-06-06T14:23:00Z"
}  
```
(Comment: There's no patient id, because the patient should stay anonymous)


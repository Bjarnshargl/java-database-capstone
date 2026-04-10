# User Story Template

## Admin User Stories

**Title:**    
As an Admin, I want to log into the portal with my username and password to manage the platform securely, so that I can manage doctors and view clinic statistics.

**Acceptance Criteria:**
1. Only I can see my password, not any other admin or anyone else.
2. Only users with an admin role can log into the admin area.

**Priority:** [High]

**Story Points:** [3]

**Notes:**
- Error messages should not reveal username or password.
- Support for password reset functionality should be considered.

---

**Title:**
As an Admin, I want to log out of the portal to protect system access, so that no one can continue working with my identity.

**Acceptance Criteria:**
1. There is a clearly visible "Logout" button accessible on every page of the admin portal.
2. After clicking the "Logout" button, my session is immediately terminated and all authentication tokens are invalidated.
3. After logging out, I am redirected to the login page.

**Priority:** [High]

**Story Points:** [2]

**Notes:**
- The logout must work reliably even if the session expires.
- Display a confirmation message after successful logout.
- Consider automatic logout after a period of inactivity for additional security.
- Ensure logout works on all supported browsers and devices.

---

**Title:**  
As an Admin, I want to add doctors to the portal, so that new medical staff can access the system and be assigned to clinics.

**Acceptance Criteria:**
1. There is an "Add Doctor" button available in the admin area, which opens a form for entering doctor details.
2. Upon submitting the form, the system validates all required fields.
3. The newly added doctor appears immediately in the doctor list, and only admins can perform this action.

**Priority:** [High]

**Story Points:** [5]

**Notes:**
- The system should prevent adding a doctor with an email address that already exists in the database.
- Error messages must be clear and user-friendly (e.g., "Email already in use").
- Consider role-based access: Only users with admin rights can add doctors.

---

**Title:**  
As an Admin, I want to delete doctor's profiles.

**Acceptance Criteria:**
1. There is a "Delete" button available next to each doctor's profile in the admin area.
2. When the "Delete" button is clicked, a confirmation dialog appears to prevent accidental deletion.
3. After confirmation, the doctor's profile is removed from the list and the doctor can no longer log in or access the system.

**Priority:** [High]

**Story Points:** [3]

**Notes:**
- Deletion should only be possible for admins.
- If the doctor is associated with active appointments or records, display a warning and require additional confirmation.
- Deleted profiles should be permanently removed, but consider implementing a "soft delete" or backup in case of accidental deletion.

---

**Title:**    
As an admin, I want to run a stored procedure in the MySQL CLI to get the number of appointments per period and track usage statistics, so that I can analyze trends and optimize resource planning.

**Acceptance Criteria:**
1. There is a stored procedure in the MySQL database that returns the number of appointments per period.
2. The stored procedure can be executed via the MySQL CLI with parameters for the period.

**Priority:** [Medium]

**Story Points:** [3]

**Notes:**
- Consider access rights: Only authorized users should be able to execute this procedure.
- Extendability: The procedure should be easy to adapt for additional statistics (e.g., by doctor or clinic) in the future.
- Handle possible NULL values and data inconsistencies in the appointments table.

## Patient User Stories

**Title:**    
As a Patient, I want to view a list of doctors without logging in to explore options before registering, so that I can decide whether the portal offers suitable doctors for my needs.

**Acceptance Criteria:**
1. There is a publicly accessible page that displays a list of all active doctors, including their names and specialties.
2. The list can be filtered by specialty and location without requiring user authentication.
3. No sensitive information is shown; only general information is visible to non-logged-in users.

**Priority:** [High]

**Story Points:** [5]

**Notes:**
- Ensure the public doctor list updates automatically when a doctor is added or removed from the system.
- Comply with data privacy regulations: Only show information that doctors have consented to make public.
- If a user tries to access more detailed information or book an appointment, redirect them to the registration/login page.

---

**Title:**    
As a Patient, I want to sign up using my email and password to book appointments, so that I can securely create an account and manage my appointments online.

**Acceptance Criteria:**
1. There is a registration form where patients can enter their email address and create a password.
2. The system validates the email for correct format and checks that the password meets security requirements.
3. After successful registration, the patient receives a confirmation email and can log in to book appointments.

**Priority:** [High]

**Story Points:** [3]

**Notes:**
- Prevent duplicate registrations with the same email address.
- Provide clear error messages for invalid input or if the email is already registered.
- Store passwords securely (e.g., hashed and salted).

---

**Title:**    
As a Patient, I want to log into the portal to manage my bookings, so that I can view, modify, or cancel my appointments conveniently and securely.

**Acceptance Criteria:**
1. There is a login page where patients can enter their email and password to access their account.
2. After successful login, patients can view a list of their current and past bookings.
3. Patients can modify or cancel upcoming appointments directly from their portal dashboard.

**Priority:** [High]

**Story Points:** [3]

**Notes:**
- Ensure secure authentication.
- Provide feedback for incorrect login credentials and allow password reset.
- Session management: Log out users after inactivity for security.

---

**Title:**    
As a Patient, I want to log out of the portal to secure my account, so that I can ensure no unauthorized person can access my personal information or bookings.

**Acceptance Criteria:**
1. There is a clearly visible "Log out" or "Sign out" button accessible from any page in the portal after logging in.
2. Clicking the log out button immediately ends the user session and redirects the patient to the login or home page.
3. After logging out, accessing restricted pages (e.g., bookings or profile) without logging in again is not possible.

**Priority:** [High]

**Story Points:** [2]

**Notes:**
- Display a confirmation or success message upon logging out.
- Properly clear all session data and cookies to prevent unauthorized access.

---

**Title:**    
As a Patient, I want to log in and book an hour-long appointment to consult with a doctor, so that I can receive medical advice and plan my visit efficiently.

**Acceptance Criteria:**
1. After logging in, patients can view available doctors and their appointment slots for consultations.
2. The system allows patients to select a doctor and an available time slot, and confirm their booking.
3. Once booked, the appointment appears in the patient's list of upcoming appointments, and the selected time slot is no longer available to others.

**Priority:** [High]

**Story Points:** [5]

**Notes:**
- Prevent double-booking: Ensure a doctor cannot have overlapping appointments.
- Send a confirmation email to the patient after successful booking.
- Allow cancellation or rescheduling according to clinic policy.
- Validate that only logged-in patients can book appointments.

---

**Title:**    
As a Patient, I want to view my upcoming appointments so that I can prepare accordingly, so that I am aware of my scheduled consultations and can manage my time effectively.

**Acceptance Criteria:**
1. After logging in, patients can access a section that displays a list of all their upcoming appointments.
2. Patients can click on each appointment for additional details).

**Priority:** [High]

**Story Points:** [3]

**Notes:**
- Ensure that only the logged-in patient can view their own appointments.
- Display clear messages if there are no upcoming appointments.
- Handle edge cases such as overlapping appointments or appointments cancelled by the doctor.

---

## Doctor User Stories

**Title:**    
As a Doctor, I want to log into the portal to manage my appointments, so that I can efficiently organize my schedule and provide timely consultations to my patients.

**Acceptance Criteria:**
1. There is a secure login page for doctors to access their accounts with email and password.
2. After successful login, doctors can view a list of their upcoming and past appointments, including patient names, appointment times, and status.
3. Doctors can modify the status of appointments.

**Priority:** [High]

**Story Points:** [3]

**Notes:**
- Ensure only authorized doctors can access the portal.
- Provide clear error messages for invalid login credentials.
- Support password reset and session management.

---

**Title:**    
As a Doctor, I want to log out of the portal to protect my data, so that unauthorized users cannot access sensitive patient or appointment information.

**Acceptance Criteria:**
1. There is a clearly visible "Log out" button accessible from any page in the portal after login.
2. Clicking the log out button immediately ends the session and redirects the doctor to the login or home page.
3. After logging out, restricted pages are no longer accessible without logging in again.

**Priority:** [High]

**Story Points:** [2]

**Notes:**
- Display a confirmation or success message after logging out.
- Properly clear all session data and cookies for security.
- Handle session expiration and automatic logout after inactivity.
- Consider audit logging of logout actions.  

---

**Title:**    
As a Doctor, I want to view my appointment calendar to stay organized, so that I can efficiently manage my schedule and provide timely care to my patients.

**Acceptance Criteria:**
1. After logging in, doctors can access a calendar view that displays all upcoming appointments.
2. Doctors can click on individual appointments for more details.

**Priority:** [High]

**Story Points:** [5]

**Notes:**
- Only authorized doctors can view their own appointment calendars.
- Display clear messages if there are no appointments on a selected day.
- Handle overlapping appointments.
- Provide filters or search for appointments by patient name or date.

---

**Title:**    
As a Doctor, I want to mark my unavailability to inform patients only about the available slots, so that patients cannot book appointments when I am not available.

**Acceptance Criteria:**
1. After logging in, doctors can select dates and times in their calendar to mark as unavailable.
2. Marked unavailable slots are immediately blocked and not offered to patients for booking.
3. Doctors can update or remove their unavailability at any time.

**Priority:** [High]

**Story Points:** [5]

**Notes:**
- Prevent patients from booking appointments in unavailable time slots.
- Display a confirmation message when unavailability is successfully set or updated.

---

**Title:**    
As a Doctor, I want to update my profile with specialization and contact information so that patients have up-to-date information, so that they can reach me easily and choose the right specialist for their needs.

**Acceptance Criteria:**
1. After logging in, doctors can access and edit their profile, including fields for specialization, contact information (phone, email), and other relevant details.
2. Changes to the profile are saved instantly and reflected in the patient portal.
3. Patients can view the updated profile information when searching for or booking appointments.

**Priority:** [High]

**Story Points:** [3]

**Notes:**
- Validate fields for correct formatting.
- Allow doctors to update additional information.
- Changes should be tracked for audit purposes.
- Handle edge cases such as incomplete or invalid information.
- Ensure only the doctor can edit their own profile.  

---

**Title:**    
As a Doctor, I want to view the patient details for upcoming appointments so that I can be prepared, so that I can provide informed and personalized care during the consultation.

**Acceptance Criteria:**
1. After logging in, doctors can select an upcoming appointment from their calendar or appointment list to view detailed patient information.
2. Access to patient details is restricted to the doctor assigned to the appointment.

**Priority:** [High]

**Story Points:** [3]

**Notes:**
- Ensure patient data is displayed in a clear and organized manner.
- Handle cases where patient information is incomplete or missing (e.g., show a warning or prompt for update).
- Ensure data is only shown for future appointments.  

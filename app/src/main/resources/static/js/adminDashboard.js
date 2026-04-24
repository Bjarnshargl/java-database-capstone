// Import Required Modules : At the top of the file, import:
import { openModal } from './components/modals.js';
import { getDoctors, filterDoctors, saveDoctor } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';
import {renderDoctorCards} from "./loggedPatient";


// Event Binding:
// When the admin clicks the "Add Doctor" button, it triggers openModal('addDoctor')
document.getElementById('addDocBtn').addEventListener('click', () => {
    openModal('addDoctor');
});

// Load Doctor Cards on Page Load:
document.addEventListener("DOMContentLoaded", () => {
    loadDoctorCards();
});

/** // copied from patientDashboard, probably needed as admin too:
document.addEventListener("DOMContentLoaded", () => {
    const btn = document.getElementById("adminSignup");
    if (btn) {
        btn.addEventListener("click", () => openModal("adminSignup"));
    }
});

document.addEventListener("DOMContentLoaded", () => {
    const loginBtn = document.getElementById("adminSignup")
    if (loginBtn) {
        loginBtn.addEventListener("click", () => {
            openModal("adminSignup")
        })
    }
})
    **/

function loadDoctorCards() {
    getDoctors()
        .then(doctors => {
            const contentDiv = document.getElementById("content");
            contentDiv.innerHTML = "";

            doctors.forEach(doctor => {
                const card = createDoctorCard(doctor);
                contentDiv.appendChild(card);
            });
        })
        .catch(error => {
            console.error("Failed to load doctors:", error);
        });
}

// Implement Search and Filter Logic:
// Filter Input
document.getElementById("searchBar").addEventListener("input", filterDoctorsOnChange);
document.getElementById("filterTime").addEventListener("change", filterDoctorsOnChange);
document.getElementById("filterSpecialty").addEventListener("change", filterDoctorsOnChange);

function filterDoctorsOnChange() {
    const searchBar = document.getElementById("searchBar").value.trim();
    const filterTime = document.getElementById("filterTime").value;
    const filterSpecialty = document.getElementById("filterSpecialty").value;


    const name = searchBar.length > 0 ? searchBar : null;
    const time = filterTime.length > 0 ? filterTime : null;
    const specialty = filterSpecialty.length > 0 ? filterSpecialty : null;

    filterDoctors(name, time, specialty)
        .then(response => {
            const doctors = response.doctors;
            const contentDiv = document.getElementById("content");
            contentDiv.innerHTML = "";

            if (doctors.length > 0) {
                console.log(doctors);
                doctors.forEach(doctor => {
                    const card = createDoctorCard(doctor);
                    contentDiv.appendChild(card);
                });
            } else {
                contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
                console.log("Nothing");
            }
        })
        .catch(error => {
            console.error("Failed to filter doctors:", error);
            alert("❌ An error occurred while filtering doctors.");
        });
}

// Handle Add Doctor Modal:
document.addEventListener("DOMContentLoaded", () => {
    const btn = document.getElementById("addDoctor");
    if (btn) {
        btn.addEventListener("click", () => openModal("addDoctor"));
    }
});

window.addDoctor = async function () {
    try {
        // Name, specialty, email, password, mobile no., availability time.
        const name = document.getElementById("name").value;
        const specialty = document.getElementById("specialty").value;
        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;
        const phone = document.getElementById("phone").value;
        const availableTimes = document.getElementById("availableTimes").value;

        const data = { name, specialty, email, password, phone, availableTimes };
        const { success, message } = await addDoctor(data);
        if (success) {
            alert(message);
            document.getElementById("modal").style.display = "none";
            window.location.reload();
        }
        else alert(message);
    } catch (error) {
        console.error("Signup failed:", error);
        alert("❌ An error occurred while signing up.");
    }
};
/*
  This script handles the admin dashboard functionality for managing doctors:
  - Loads all doctor cards
  - Filters doctors by name, time, or specialty
  - Adds a new doctor via modal form


  Attach a click listener to the "Add Doctor" button
  When clicked, it opens a modal form using openModal('addDoctor')


  When the DOM is fully loaded:
    - Call loadDoctorCards() to fetch and display all doctors


  Function: loadDoctorCards
  Purpose: Fetch all doctors and display them as cards

    Call getDoctors() from the service layer
    Clear the current content area
    For each doctor returned:
    - Create a doctor card using createDoctorCard()
    - Append it to the content div

    Handle any fetch errors by logging them


  Attach 'input' and 'change' event listeners to the search bar and filter dropdowns
  On any input change, call filterDoctorsOnChange()


  Function: filterDoctorsOnChange
  Purpose: Filter doctors based on name, available time, and specialty

    Read values from the search bar and filters
    Normalize empty values to null
    Call filterDoctors(name, time, specialty) from the service

    If doctors are found:
    - Render them using createDoctorCard()
    If no doctors match the filter:
    - Show a message: "No doctors found with the given filters."

    Catch and display any errors with an alert


  Function: renderDoctorCards
  Purpose: A helper function to render a list of doctors passed to it

    Clear the content area
    Loop through the doctors and append each card to the content area


  Function: adminAddDoctor
  Purpose: Collect form data and add a new doctor to the system

    Collect input values from the modal form
    - Includes name, email, phone, password, specialty, and available times

    Retrieve the authentication token from localStorage
    - If no token is found, show an alert and stop execution

    Build a doctor object with the form values

    Call saveDoctor(doctor, token) from the service

    If save is successful:
    - Show a success message
    - Close the modal and reload the page

    If saving fails, show an error message
*/

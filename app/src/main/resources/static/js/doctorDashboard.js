// Import Required Modules : At the top of the file, import:
import { getAppointments} from "./components/appointmentRow";
import { createPatientRow} from "./components/patientRows";
import {openModal} from "./components/modals";
import {getAppointmentRecord} from "./services/appointmentRecordService";

// Initialize Global Variables:
const tableBody = document.getElementById("patientTableBody");
const today = new Date();
const yyyy = today.getFullYear();
const mm = String(today.getMonth() + 1).padStart(2, '0');
const dd = String(today.getDate()).padStart(2, '0');
const selectedDate = `${yyyy}-${mm}-${dd}`;
//const selectedDate = new Date().toDateString().split('-')[0];
const todayButton = document.getElementById("todayButton");

console.log(selectedDate); // z.B. "2024-06-09"
const token = localStorage.getItem("token");
const patientName = document.getElementById("patientName").value;

let allAppointments = [];
let filteredAppointments = [];
let patientId = null;

// Setup Search Bar Functionality:
// Search and Filter Listeners
document.getElementById("searchBar").addEventListener("input", handleFilterChange);
document.getElementById("appointmentFilter").addEventListener("change", handleFilterChange);

async function handleFilterChange() {
    const searchBarValue = document.getElementById("searchBar").value.trim();
    const filterValue = document.getElementById("appointmentFilter").value;

    const name = searchBarValue || null;
    const condition = filterValue === "allAppointments" ? null : filterValue || null;

    try {
        const response = await filterAppointments(condition, name, token);
        const appointments = response?.appointments || [];
        filteredAppointments = appointments.filter(app => app.patientId === patientId);

        renderAppointments(filteredAppointments);
    } catch (error) {
        console.error("Failed to filter appointments:", error);
        alert("❌ An error occurred while filtering appointments.");
    }
}

function renderAppointments(appointments) {
    tableBody.innerHTML = "";

    const actionTh = document.querySelector("#patientTable thead tr th:last-child");
    if (actionTh) {
        actionTh.style.display = "table-cell"; // Always show "Actions" column
    }

    if (!appointments.length) {
        tableBody.innerHTML = `<tr><td colspan="5" style="text-align:center;">No Appointments Found</td></tr>`;
        return;
    }

    appointments.forEach(appointment => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${appointment.patientName || "You"}</td>
      <td>${appointment.doctorName}</td>
      <td>${appointment.appointmentDate}</td>
      <td>${appointment.appointmentTimeOnly}</td>
      <td>${appointment.status == 0 ? `<img src="../assets/images/edit/edit.png" alt="Edit" class="prescription-btn" data-id="${appointment.patientId}">` : "-"}</td>
    `;

        if (appointment.status == 0) {
            const actionBtn = tr.querySelector(".prescription-btn");
            actionBtn?.addEventListener("click", () => redirectToUpdatePage(appointment));
        }

        tableBody.appendChild(tr);
    });
}

// Bind Event Listeners to Filter Controls:
document.addEventListener("DOMContentLoaded", () => {
    const loginBtn = document.getElementById("patientLogin")
    if (todayButton) {
        todayButton.addEventListener("click", () => {
            document.getElementById("datePicker").value = selectedDate;
            loadAppointments();
        })
    }
})

async function loadAppointments(filter = "upcoming") {
    const appointments = await getAppointmentRecord();

    if (!appointments || appointments.length === 0) {
        tableBody.innerHTML = `<tr><td class="noPatientRecord" colspan='5'>No appointments found.</td></tr>`;
        return;
    }

    const today = new Date().setHours(0, 0, 0, 0);
    let filteredAppointments = appointments;

    if (filter === "upcoming") {
        filteredAppointments = appointments.filter(app => new Date(app.date) >= today);
    } else if (filter === "past") {
        filteredAppointments = appointments.filter(app => new Date(app.date) < today);
    }

    if (filteredAppointments.length === 0) {
        tableBody.innerHTML = `<tr><td class="noPatientRecord" colspan='5'>No ${filter} appointments found.</td></tr>`;
        return;
    }

    tableBody.innerHTML = "";
    filteredAppointments.forEach(appointment => {
        const row = getAppointments(appointment);
        tableBody.appendChild(row);
    });
}


/*
  Import getAllAppointments to fetch appointments from the backend
  Import createPatientRow to generate a table row for each patient appointment


  Get the table body where patient rows will be added
  Initialize selectedDate with today's date in 'YYYY-MM-DD' format
  Get the saved token from localStorage (used for authenticated API calls)
  Initialize patientName to null (used for filtering by name)


  Add an 'input' event listener to the search bar
  On each keystroke:
    - Trim and check the input value
    - If not empty, use it as the patientName for filtering
    - Else, reset patientName to "null" (as expected by backend)
    - Reload the appointments list with the updated filter


  Add a click listener to the "Today" button
  When clicked:
    - Set selectedDate to today's date
    - Update the date picker UI to match
    - Reload the appointments for today


  Add a change event listener to the date picker
  When the date changes:
    - Update selectedDate with the new value
    - Reload the appointments for that specific date


  Function: loadAppointments
  Purpose: Fetch and display appointments based on selected date and optional patient name

  Step 1: Call getAllAppointments with selectedDate, patientName, and token
  Step 2: Clear the table body content before rendering new rows

  Step 3: If no appointments are returned:
    - Display a message row: "No Appointments found for today."

  Step 4: If appointments exist:
    - Loop through each appointment and construct a 'patient' object with id, name, phone, and email
    - Call createPatientRow to generate a table row for the appointment
    - Append each row to the table body

  Step 5: Catch and handle any errors during fetch:
    - Show a message row: "Error loading appointments. Try again later."


  When the page is fully loaded (DOMContentLoaded):
    - Call renderContent() (assumes it sets up the UI layout)
    - Call loadAppointments() to display today's appointments by default
*/

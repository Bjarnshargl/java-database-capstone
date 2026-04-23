import { openModal } from '../components/modals.js';
import { API_BASE_URL } from '../config/config.js';

const ADMIN_API = API_BASE_URL + '/admin';
const DOCTOR_API = API_BASE_URL + '/doctor/login'

window.onload = function () {
    const adminBtn = document.getElementById('adminLogin');
    if (adminBtn) {
        adminBtn.addEventListener('click', () => {
            openModal('adminLogin');
        });
    }

    const doctorBtn = document.getElementById('doctorLogin');
    if (doctorBtn) {
        doctorBtn.addEventListener('click', () => {
            openModal('adminLogin');
        });
    }
}

// For logging in Admin
export async function adminLoginHandler(data) {
    console.log("AdminLogin :: ", data);
    return await fetch(`${Admin_API}/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    });
}

window.loginAdmin = async function () {
    try {
        const email = document.getElementById("username").value;
        const password = document.getElementById("password").value;

        const data = {
            email,
            password
        };
        console.log("loginAdmin :: ", data);

        const response = await adminLoginHandler(data);
        console.log("Status Code:", response.status);
        console.log("Response OK:", response.ok);

        if (response.ok) {
            const result = await response.json();
            console.log(result);
            selectRole('admin');
            localStorage.setItem('token', result.token);
            window.location.href = '/pages/adminDashboard.html';
        } else {
            alert('❌ Invalid credentials!');
        }
    } catch (error) {
        alert("❌ Failed to Login: " + error);
        console.log("Error :: loginAdmin :: ", error);
    }
};

// For logging in Doctor
export async function DoctorLoginHandler(data) {
    console.log("DoctorLogin :: ", data);
    return await fetch(`${Doctor_API}/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    });
}

window.loginDoctor = async function () {
    try {
        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;

        const data = {
            email,
            password
        };
        console.log("loginDoctor :: ", data);

        const response = await DoctorLoginHandler(data);
        console.log("Status Code:", response.status);
        console.log("Response OK:", response.ok);

        if (response.ok) {
            const result = await response.json();
            console.log(result);
            selectRole('doctor');
            localStorage.setItem('token', result.token);
            window.location.href = '/pages/doctorDashboard.html';
        } else {
            alert('❌ Invalid credentials!');
        }
    } catch (error) {
        alert("❌ Failed to Login: " + error);
        console.log("Error :: loginDoctor :: ", error);
    }
};


/*
  Import the openModal function to handle showing login popups/modals
  Import the base API URL from the config file

  Done

  Define constants for the admin and doctor login API endpoints using the base URL

  Done

  Use the window.onload event to ensure DOM elements are available after page load
  Inside this function:
    - Select the "adminLogin" and "doctorLogin" buttons using getElementById
    - If the admin login button exists:
        - Add a click event listener that calls openModal('adminLogin') to show the admin login modal
    - If the doctor login button exists:
        - Add a click event listener that calls openModal('doctorLogin') to show the doctor login modal

  Done

  Define a function named adminLoginHandler on the global window object
  This function will be triggered when the admin submits their login credentials

  Done

  Step 1: Get the entered username and password from the input fields
  Step 2: Create an admin object with these credentials

  Done

  Step 3: Use fetch() to send a POST request to the ADMIN_API endpoint
    - Set method to POST
    - Add headers with 'Content-Type: application/json'
    - Convert the admin object to JSON and send in the body

    Done

  Step 4: If the response is successful:
    - Parse the JSON response to get the token
    - Store the token in localStorage
    - Call selectRole('admin') to proceed with admin-specific behavior

    Done

  Step 5: If login fails or credentials are invalid:
    - Show an alert with an error message

    Done

  Step 6: Wrap everything in a try-catch to handle network or server errors
    - Show a generic error message if something goes wrong

    Done


  Define a function named doctorLoginHandler on the global window object
  This function will be triggered when a doctor submits their login credentials

  Done

  Step 1: Get the entered email and password from the input fields
  Step 2: Create a doctor object with these credentials

  Done

  Step 3: Use fetch() to send a POST request to the DOCTOR_API endpoint
    - Include headers and request body similar to admin login

    Done

  Step 4: If login is successful:
    - Parse the JSON response to get the token
    - Store the token in localStorage
    - Call selectRole('doctor') to proceed with doctor-specific behavior

    Done

  Step 5: If login fails:
    - Show an alert for invalid credentials

    Done

  Step 6: Wrap in a try-catch block to handle errors gracefully
    - Log the error to the console
    - Show a generic error message

    Done
*/

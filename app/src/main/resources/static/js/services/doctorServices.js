// doctorServices
import { API_BASE_URL } from "../config/config.js";
const DOCTOR_API = API_BASE_URL + '/doctor'

// Create a Function to Get All Doctors
export async function getDoctors() {
    // Handles any errors using a try-catch block.
    try {
        // Sends a GET request to the doctor endpoint.
        // Awaits a response from the server.
        // Extracts and returns the list of doctors from the response JSON.
        const response = await fetch(DOCTOR_API, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            return [];
        }
        return await response.json();

    } catch (error) {
        // Returns an empty list ([]) if something goes wrong to avoid breaking the frontend.
        return [];
    }
}

// Create a Function to Delete a Doctor
// Takes the doctor’s unique id and an authentication token (for security).
// This allows an authenticated Admin to remove doctors from the system securely.
export async function deleteDoctor(id, token) {
    // Catches and handles any errors to prevent frontend crashes.
    try {
        // Constructs the full endpoint URL using the ID and token.
        const response = await fetch(`${DOCTOR_API}/${id}/${token}`, {
            // Sends a DELETE request to that endpoint.
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        const data = await response.json();

        //Parses the JSON response and returns a success status and message.
        if (response.ok && data.success) {
            return { success: true };
        } else {
            return { success: false, error: data.error || 'Unknown Error' };
        }

    // Catches and handles any errors to prevent frontend crashes.
    } catch (error) {
        return { success: false, error: error.message };
    }
}

// Create a Function to Save (Add) a New Doctor
// Accept a doctor object containing all doctor details (like name, email, availability).
// Also take in a token for Admin authentication.
export async function saveDoctor(doctor, token) {
    try {
        // Send a POST request with headers specifying JSON data.
        // Include the doctor data in the request body (converted to JSON).
        const response = await fetch(`${DOCTOR_API}/${token}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(doctor)
        });

        const data = await response.json();

        // Return a structured response with success and message.
        if (response.ok && data.success) {
            return { success: true, message: data.message || "Doctor saved!" };
        // Catch and log any errors to help during debugging
        } else {
            return { success: false, error: data.error || "Error on saving." };
        }

    // Return a structured response with success and message.
    // Catch and log any errors to help during debugging
    } catch (error) {
        return { success: false, error: error.message || "Unknown Error." };
    }
}

// Create a Function to Filter Doctors
// Accepts parameters like name, time, and specialty.
export async function filterDoctors(name, time, specialty) {
    try {
        //
        const params = new URLSearchParams();
        if (name) params.append('name', name);
        if (time) params.append('time', time);
        if (specialty) params.append('specialty', specialty);

        // Constructs a GET request URL by passing these values as route parameters.
        // Sends a GET request to retrieve matching doctor records.
        const url = `${DOCTOR_API}/filter?${params.toString()}`;
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        // Returns the filtered list of doctors (or an empty list if none are found).
        if (!response.ok) {
            return [];
        }

        const data = await response.json();
        // Returns the filtered list of doctors (or an empty list if none are found).
        return data.doctors || [];

    // Returns the filtered list of doctors (or an empty list if none are found).
    } catch (error) {
        return [];
    }
}

/*
  Import the base API URL from the config file
  Define a constant DOCTOR_API to hold the full endpoint for doctor-related actions

  Done

  Function: getDoctors
  Purpose: Fetch the list of all doctors from the API

   Use fetch() to send a GET request to the DOCTOR_API endpoint
   Convert the response to JSON
   Return the 'doctors' array from the response
   If there's an error (e.g., network issue), log it and return an empty array

   Done


  Function: deleteDoctor
  Purpose: Delete a specific doctor using their ID and an authentication token

   Use fetch() with the DELETE method
    - The URL includes the doctor ID and token as path parameters
   Convert the response to JSON
   Return an object with:
    - success: true if deletion was successful
    - message: message from the server
   If an error occurs, log it and return a default failure response

   Done


  Function: saveDoctor
  Purpose: Save (create) a new doctor using a POST request

   Use fetch() with the POST method
    - URL includes the token in the path
    - Set headers to specify JSON content type
    - Convert the doctor object to JSON in the request body

   Parse the JSON response and return:
    - success: whether the request succeeded
    - message: from the server

   Catch and log errors
    - Return a failure response if an error occurs

    Done


  Function: filterDoctors
  Purpose: Fetch doctors based on filtering criteria (name, time, and specialty)

   Use fetch() with the GET method
    - Include the name, time, and specialty as URL path parameters
   Check if the response is OK
    - If yes, parse and return the doctor data
    - If no, log the error and return an object with an empty 'doctors' array

   Catch any other errors, alert the user, and return a default empty result

   Done
*/

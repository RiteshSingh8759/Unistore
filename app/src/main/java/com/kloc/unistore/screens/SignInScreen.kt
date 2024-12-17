package com.kloc.unistore.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kloc.unistore.firestoredb.viewmodel.EmployeeViewModel
import com.kloc.unistore.mail.EmailSender
import com.kloc.unistore.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.mail.MessagingException


@Composable
fun SignInScreen(navController: NavController, employeeViewModel: EmployeeViewModel ) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // State variables
    var staff_id by remember { mutableStateOf("") }
    var generatedOtp by remember { mutableStateOf("") }
    var userInputOtp by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var staffError by remember { mutableStateOf("") }
    var staffNotRegistered by remember { mutableStateOf(false) }

    // Observe the result of checking the email existence in the ViewModel
    val userData = employeeViewModel.res1.value
    Log.d("debug","${userData.data?.employee}")
    // Handle email check when the result changes
    LaunchedEffect(staff_id) {
        if (staff_id.isNotEmpty() ) {
            employeeViewModel.getUserByEmail(staff_id.trim().uppercase()) // Call to get user by email
        }
    }

    // Handle when userData changes
    LaunchedEffect(userData) {
        if (userData != null) {
            staffNotRegistered = userData.data == null // Set emailNotRegistered based on data
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sign In", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = staff_id,
            onValueChange = { staff_id = it },
            label = { Text("Enter StaffId") },
            isError = staffError.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )

        // Display error message if email is invalid
        if (staffError.isNotEmpty()) {
            Text(
                text = staffError,
                color = Color.Red,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // Display message if the email is not registered
        if (staffNotRegistered) {
            Text(
                text = "staff_id is not registered to Unistore.",
                color = Color.Red,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!otpSent) {
            Button(onClick = {
                // Check if the staff exists in the database
                if (staff_id.isNotEmpty() ) {
                    staffError = "" // Clear error if valid staffId
                    if (userData.data == null) {
                        staffNotRegistered = true // staff not found in the database
                    } else {
                        // Reset error state if email is valid
                        staffNotRegistered = false
                        // Generate OTP and send email
                        coroutineScope.launch(Dispatchers.IO) {
                            try {
                                generatedOtp = (100000..999999).random().toString()
                                // Send OTP via email
                                EmailSender.sendEmail(
                                    toEmail = userData.data.employee?.email,
                                    subject = "Your OTP Code",
                                    body = "Your OTP is: $generatedOtp",
                                    fromEmail = "ritesh.singh@kloctechnologies.com",
                                    password = "xjwfxiseksaouxue" // Securely store this!
                                )

                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "OTP Sent to your email.", Toast.LENGTH_SHORT).show()
                                    otpSent = true
                                }
                            } catch (e: MessagingException) {
                                Log.e("EmailSender", "Error sending email: ${e.message}")
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Error sending OTP. Try again.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                } else {
                    staffError = "Please enter a valid staff id."
                }
            }) {
                Text("Send OTP")
            }
        } else {
            OutlinedTextField(
                value = userInputOtp,
                onValueChange = { userInputOtp = it },
                label = { Text("Enter OTP") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                if (userInputOtp == generatedOtp) {
                    Toast.makeText(context, "OTP Verified!", Toast.LENGTH_SHORT).show()
                    navController.navigate(route = Screen.SchoolDetailsScreen.route)
                } else {
                    Toast.makeText(context, "Invalid OTP. Try again.", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Verify OTP")
            }
        }
    }
}
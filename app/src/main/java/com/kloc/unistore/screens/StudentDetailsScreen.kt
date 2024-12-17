package com.kloc.unistore.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kloc.unistore.common.CommonDropdownMenu
import com.kloc.unistore.entity.student.Student
import com.kloc.unistore.model.viewModel.MainViewModel
import com.kloc.unistore.navigation.Screen

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StudentDetailsScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val studentDetails = mainViewModel.studentViewModel.studentDetails.value

    var studentName by remember { mutableStateOf(studentDetails?.studentName ?: "a") }
    var parentName by remember { mutableStateOf(studentDetails?.parentName ?: "a") }
    var phoneNumber by remember { mutableStateOf(studentDetails?.phoneNumber ?: "a") }
    var emailAddress by remember { mutableStateOf(studentDetails?.emailAddress ?: "a@gmail.com") }
    var billingAddress by remember { mutableStateOf(studentDetails?.billingAddress ?: "wq") }
    var shippingAddress by remember { mutableStateOf(studentDetails?.shipingAddress ?: "q") }
    var city by remember { mutableStateOf(studentDetails?.city ?: "s") }
    var state by remember { mutableStateOf(studentDetails?.state ?: "s") }
    var zipCode by remember { mutableStateOf(studentDetails?.zipCode ?: "s") }

    var selectedClass by remember { mutableStateOf(studentDetails?.selectedClass ?: "s") }
    var selectedGender by remember { mutableStateOf(studentDetails?.gender ?: "s") }



    var expandedClass by remember { mutableStateOf(false) }
    var expandedPaymentMethod by remember { mutableStateOf(false) }
    var expandedGender by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            Button(colors=  androidx.compose.material.ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                onClick = {
                    // Validate fields
                    if (studentName.isBlank() || parentName.isBlank() ||
                        phoneNumber.isBlank() || emailAddress.isBlank() ||
                        billingAddress.isBlank() || shippingAddress.isBlank() ||
                        city.isBlank() || state.isBlank() || zipCode.isBlank() ||
                        selectedClass.isBlank() ||
                        selectedGender.isBlank()
                    ) {
                        Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                    } else {
                        val newStudent = Student(
                            studentName = studentName,
                            parentName = parentName,
                            phoneNumber = phoneNumber,
                            emailAddress = emailAddress,
                            billingAddress = billingAddress,
                            shipingAddress = shippingAddress,
                            city = city,
                            state = state,
                            zipCode = zipCode,
                            selectedClass = selectedClass,
                            gender = selectedGender
                        )
                        mainViewModel.studentViewModel.saveStudentDetails(newStudent)// After saving student details, navigate to OrderDetailsScreen and remove StudentDetailsScreen from the back stack
                        navController.navigate(Screen.OrderDetailsScreen.route) {
                            // Pop up to CartScreen, inclusive if needed, but remove StudentDetailsScreen from back stack
                            popUpTo(Screen.StudentDetailsScreen.route) { inclusive = true }
                            launchSingleTop = true  // To prevent adding multiple instances of OrderDetailsScreen if already on top
                        }

                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Save Student Details",color= Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Static Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Student Details",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            // Scrollable Fields
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = studentName,
                        onValueChange = { studentName = it },
                        label = { Text("Student Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = parentName,
                        onValueChange = { parentName = it },
                        label = { Text("Parent Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                }
                item {
                    OutlinedTextField(
                        value = emailAddress,
                        onValueChange = { emailAddress = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                }
                item {
                    OutlinedTextField(
                        value = billingAddress,
                        onValueChange = { billingAddress = it },
                        label = { Text("Billing Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = shippingAddress,
                        onValueChange = { shippingAddress = it },
                        label = { Text("Shipping Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = state,
                        onValueChange = { state = it },
                        label = { Text("State") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = zipCode,
                        onValueChange = { zipCode = it },
                        label = { Text("Zip Code") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    CommonDropdownMenu(
                        label = "Class",
                        items = (1..10).map { "Class $it" },
                        selectedItem = selectedClass,
                        onItemSelected = { selectedClass = it }
                    )
                }
                item {
                    CommonDropdownMenu(
                        label = "Gender",
                        items = listOf("Male", "Female", "Other"),
                        selectedItem = selectedGender,
                        onItemSelected = { selectedGender = it }
                    )
                }
            }
        }
    }
}

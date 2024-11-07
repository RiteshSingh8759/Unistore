package com.kloc.unistore.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kloc.unistore.entity.student.Student
import com.kloc.unistore.model.viewModel.MainViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StudentDetailsScreen(navController: NavHostController, mainViewModel: MainViewModel) {
    var studentName by remember { mutableStateOf("") }
    var parentName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var emailAddress by remember { mutableStateOf("") }

    var selectedClass by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("") }

    var expandedClass by remember { mutableStateOf(false) }
    var expandedPaymentMethod by remember { mutableStateOf(false) }
    var expandedGender by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Student Details",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Text Fields
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
                ExposedDropdownMenuBox(expanded = expandedClass, onExpandedChange = { expandedClass = !expandedClass }) {
                    OutlinedTextField(
                        value = selectedClass,
                        onValueChange = { selectedClass = it },
                        label = { Text("Class") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                        }
                    )
                    ExposedDropdownMenu(expanded = expandedClass, onDismissRequest = { expandedClass = false }) {
                        listOf("Class 1", "Class 2", "Class 3").forEach { option ->
                            DropdownMenuItem(onClick = {
                                selectedClass = option
                                expandedClass = false
                            }) {
                                Text(option)
                            }
                        }
                    }
                }
            }

            item {
                // Payment Method Dropdown
                ExposedDropdownMenuBox(expanded = expandedPaymentMethod, onExpandedChange = { expandedPaymentMethod = !expandedPaymentMethod }) {
                    OutlinedTextField(
                        value = selectedPaymentMethod,
                        onValueChange = { selectedPaymentMethod = it },
                        label = { Text("Payment Method") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                        }
                    )
                    ExposedDropdownMenu(expanded = expandedPaymentMethod, onDismissRequest = { expandedPaymentMethod = false }) {
                        listOf("Cash", "Card", "Online Transfer").forEach { option ->
                            DropdownMenuItem(onClick = {
                                selectedPaymentMethod = option
                                expandedPaymentMethod = false
                            }) {
                                Text(option)
                            }
                        }
                    }
                }
            }

            item {
                // Gender Dropdown
                ExposedDropdownMenuBox(expanded = expandedGender, onExpandedChange = { expandedGender = !expandedGender }) {
                    OutlinedTextField(
                        value = selectedGender,
                        onValueChange = { selectedGender = it },
                        label = { Text("Gender") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                        }
                    )
                    ExposedDropdownMenu(expanded = expandedGender, onDismissRequest = { expandedGender = false }) {
                        listOf("Male", "Female", "Other").forEach { option ->
                            DropdownMenuItem(onClick = {
                                selectedGender = option
                                expandedGender = false
                            }) {
                                Text(option)
                            }
                        }
                    }
                }
            }
        }

        // Save Button at the bottom center
        Button(
            onClick = {
                val newStudent = Student(
                    studentName = studentName,
                    parentName = parentName,
                    phoneNumber = phoneNumber,
                    emailAddress = emailAddress,
                    selectedClass = selectedClass,
                    paymentMethod = selectedPaymentMethod,
                    gender = selectedGender
                )
                mainViewModel.studentViewModel.saveStudentDetails(newStudent)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("Save Student Details")
        }
    }

}

package com.kloc.unistore.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dokar.sonner.ToastType
import com.kloc.unistore.common.CommonDropdownMenu
import com.kloc.unistore.common.LoadingButton
import com.kloc.unistore.entity.student.Student
import com.kloc.unistore.model.viewModel.MainViewModel
import com.kloc.unistore.navigation.Screen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavHostController


@Composable
fun StudentDetailsScreen(navController: NavHostController, mainViewModel: MainViewModel){
    val context = LocalContext.current
    val studentDetails = mainViewModel.studentViewModel.studentDetails.value

    var isLoading by remember { mutableStateOf(false) }
    var studentName by remember { mutableStateOf(studentDetails?.studentName ?: "Student Name") }
    var parentName by remember { mutableStateOf(studentDetails?.parentName ?: "Parent Name") }
    var phoneNumber by remember { mutableStateOf(studentDetails?.phoneNumber ?: "1234567890") }
    var emailAddress by remember { mutableStateOf(studentDetails?.emailAddress ?: "example@gmail.com") }

    // Billing Address Fields
    var billingAddressLine1 by remember { mutableStateOf(studentDetails?.billingAddressLine1 ?: "billing Address1") }
    var billingAddressLine2 by remember { mutableStateOf(studentDetails?.billingAddressLine2 ?: "billing address2") }
    var billingCity by remember { mutableStateOf(studentDetails?.billingCity ?: "billing city") }
    var billingState by remember { mutableStateOf(studentDetails?.billingState ?: "billing State") }
    var billingZipCode by remember { mutableStateOf(studentDetails?.billingZipCode ?: "121212") }

    // Shipping Address Fields
    var shippingAddressLine1 by remember { mutableStateOf(studentDetails?.shippingAddressLine1 ?: "billing Address1") }
    var shippingAddressLine2 by remember { mutableStateOf(studentDetails?.shippingAddressLine2 ?: "billing address2") }
    var shippingCity by remember { mutableStateOf(studentDetails?.shippingCity ?: "billing city") }
    var shippingState by remember { mutableStateOf(studentDetails?.shippingState ?: "billing State") }
    var shippingZipCode by remember { mutableStateOf(studentDetails?.shippingZipCode ?: "121212") }
    var selectedClass by remember { mutableStateOf(studentDetails?.selectedClass ?: "Class1") }
    var selectedGender by remember { mutableStateOf(studentDetails?.gender ?: "Male") }
    var note by remember { mutableStateOf(studentDetails?.note ?: "Customer note") }

    // Track checkbox state
    var isSameShippingAndBilling by remember { mutableStateOf(true) }

    val nameRegex = "^[A-Za-zÀ-ÿ\\s'-.]+$".toRegex()
    val phoneNumberRegex = "^[+]?[0-9]{10,13}$".toRegex()
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    val zipCodeRegex = "^[0-9]{5,6}$".toRegex()
    val addressRegex = "^[A-Za-z0-9\\s,.'-]{5,}$".toRegex()

    val isValidForm = studentName.matches(nameRegex) && parentName.matches(nameRegex) &&
            phoneNumber.matches(phoneNumberRegex) && emailAddress.matches(emailRegex) &&
            ((billingAddressLine1.matches(addressRegex) && billingCity.matches(addressRegex) &&
                    billingState.matches(addressRegex) && billingZipCode.matches(zipCodeRegex) &&
                    shippingAddressLine1.matches(addressRegex) && shippingCity.matches(addressRegex) &&
                    shippingState.matches(addressRegex) && shippingZipCode.matches(zipCodeRegex)) || mainViewModel.skipAddress) &&
            selectedClass.isNotBlank() && selectedGender.isNotBlank()

    // Create mutable state for tracking whether a TextField has been touched
    var isStudentNameTouched by remember { mutableStateOf(false) }
    var isParentNameTouched by remember { mutableStateOf(false) }
    var isPhoneNumberTouched by remember { mutableStateOf(false) }
    var isEmailAddressTouched by remember { mutableStateOf(false) }
    var isBillingAddressLine1Touched by remember { mutableStateOf(false) }
    var isBillingCityTouched by remember { mutableStateOf(false) }
    var isBillingStateTouched by remember { mutableStateOf(false) }
    var isBillingZipCodeTouched by remember { mutableStateOf(false) }
    var isShippingAddressLine1Touched by remember { mutableStateOf(false) }
    var isShippingCityTouched by remember { mutableStateOf(false) }
    var isShippingStateTouched by remember { mutableStateOf(false) }
    var isShippingZipCodeTouched by remember { mutableStateOf(false) }
    var isClassTouched by remember { mutableStateOf(false) }


    // Function to update shipping address when checkbox is checked
    val updateShippingAddress = {
        if (isSameShippingAndBilling) {
            shippingAddressLine1 = billingAddressLine1
            shippingAddressLine2 = billingAddressLine2
            shippingCity = billingCity
            shippingState = billingState
            shippingZipCode = billingZipCode
        } else {
            isShippingAddressLine1Touched = false
            isShippingCityTouched = false
            isShippingStateTouched = false
            isShippingZipCodeTouched = false
            shippingAddressLine1 = ""
            shippingAddressLine2 = ""
            shippingCity = ""
            shippingState = ""
            shippingZipCode = ""
        }
    }
    LaunchedEffect(isSameShippingAndBilling) {
        updateShippingAddress()
    }

    Scaffold(
        bottomBar = {
            LoadingButton(
                text = "Save Student Details",
                isLoading = isLoading,
                isEnabled = isValidForm,
                onClick = {
                    isLoading = true
                    val newStudent = Student(
                        studentName = studentName,
                        parentName = parentName,
                        phoneNumber = phoneNumber,
                        emailAddress = emailAddress,
                        billingAddressLine1 = billingAddressLine1,
                        billingAddressLine2 = billingAddressLine2,
                        billingCity = billingCity,
                        billingState = billingState,
                        billingZipCode = billingZipCode,
                        shippingAddressLine1 = shippingAddressLine1,
                        shippingAddressLine2 = shippingAddressLine2,
                        shippingCity = shippingCity,
                        shippingState = shippingState,
                        shippingZipCode = shippingZipCode,
                        selectedClass = selectedClass,
                        gender = selectedGender,
                        note = note
                    )
                    mainViewModel.studentViewModel.saveStudentDetails(newStudent)
                    isLoading = false
                    navController.navigate(Screen.OrderDetailsScreen.route) {
                        popUpTo(Screen.StudentDetailsScreen.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            // Scrollable Fields
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item {
                    OutlinedTextField(
                        value = studentName,
                        onValueChange = { studentName = it; isStudentNameTouched = true },
                        label = { Text("Student Name") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = isStudentNameTouched && !studentName.matches(nameRegex)
                    )
                    if (isStudentNameTouched && !studentName.matches(nameRegex)) {
                        Text(
                            text = "Invalid name. Only alphabets, accents, spaces, apostrophes, and hyphens are allowed.",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = parentName,
                        onValueChange = { parentName = it; isParentNameTouched = true },
                        label = { Text("Parent Name") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = isParentNameTouched && !parentName.matches(nameRegex)
                    )
                    if (isParentNameTouched && !parentName.matches(nameRegex)) {
                        Text(
                            text = "Invalid name. Only alphabets, accents, spaces, apostrophes, and hyphens are allowed.",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it; isPhoneNumberTouched = true },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = isPhoneNumberTouched && !phoneNumber.matches(phoneNumberRegex)
                    )
                    if (isPhoneNumberTouched && !phoneNumber.matches(phoneNumberRegex)) {
                        Text(
                            text = "Invalid phone number. Enter a valid phone number (10-13 digits).",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = emailAddress,
                        onValueChange = { emailAddress = it; isEmailAddressTouched = true },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = isEmailAddressTouched && !emailAddress.matches(emailRegex)
                    )
                    if (isEmailAddressTouched && !emailAddress.matches(emailRegex)) {
                        Text(
                            text = "Invalid email address. Enter a valid email format (e.g., example@domain.com).",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }


                item {
                    OutlinedTextField(
                        value = selectedClass,
                        onValueChange = { selectedClass = it; isClassTouched = true },
                        label = { Text("Line 1") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = isClassTouched
                    )
                    if (isBillingAddressLine1Touched && selectedClass.isEmpty()) {
                        Text(
                            text = "Class is mandatory",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }
                item {
                    CommonDropdownMenu(
                        label = "Gender",
                        items = listOf("Male", "Female", "Other"),
                        selectedItem = selectedGender,
                        onItemSelected = { selectedGender = it })
                }
                item {
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Note") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Address", fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Only school delivery!", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.width(4.dp))
                            Switch(checked = mainViewModel.skipAddress, onCheckedChange = { mainViewModel.skipAddress = it }, colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary))
                        }
                    }
                }

                if (!mainViewModel.skipAddress) {
                    item {
                        OutlinedTextField(
                            value = billingAddressLine1,
                            onValueChange = { billingAddressLine1 = it; isBillingAddressLine1Touched = true },
                            label = { Text("Line 1") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = isBillingAddressLine1Touched && !billingAddressLine1.matches(addressRegex)
                        )
                        if (isBillingAddressLine1Touched && !billingAddressLine1.matches(addressRegex)) {
                            Text(
                                text = "Invalid address. Only letters, numbers, spaces, commas, periods, apostrophes, and hyphens are allowed. Minimum 5 characters.",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = billingAddressLine2,
                            onValueChange = { billingAddressLine2 = it },
                            label = { Text("Line 2") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = billingCity,
                            onValueChange = { billingCity = it; isBillingCityTouched = true },
                            label = { Text("City") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = isBillingCityTouched && !billingCity.matches(addressRegex)
                        )
                        if (isBillingCityTouched && !billingCity.matches(addressRegex)) {
                            Text(
                                text = "Invalid city. Only letters, numbers, spaces, commas, periods, apostrophes, and hyphens are allowed.",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = billingState,
                            onValueChange = { billingState = it; isBillingStateTouched = true },
                            label = { Text("State") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = isBillingStateTouched && !billingState.matches(addressRegex)
                        )
                        if (isBillingStateTouched && !billingState.matches(addressRegex)) {
                            Text(
                                text = "Invalid state. Only letters, numbers, spaces, commas, periods, apostrophes, and hyphens are allowed.",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = billingZipCode,
                            onValueChange = { billingZipCode = it; isBillingZipCodeTouched = true },
                            label = { Text("Zip Code") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = isBillingZipCodeTouched && !billingZipCode.matches(zipCodeRegex)
                        )
                        if (billingZipCode.isNotEmpty() && !billingZipCode.matches(zipCodeRegex)) {
                            Text(
                                text = "Invalid zip code. Enter a valid zip code.",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }
                    }
//                    item {
//                        Row(
//                            modifier = Modifier.padding(4.dp),
//                            horizontalArrangement = Arrangement.Start,
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Checkbox(
//                                checked = isSameShippingAndBilling,
//                                onCheckedChange = { isSameShippingAndBilling = it })
//                            Spacer(modifier = Modifier.width(8.dp))
//                            Text(text = "Shipping Address same as Billing Address")
//                        }
//                    }
                    if (!isSameShippingAndBilling) {

                        item {
                            Text(text = "Shipping Address")
                            OutlinedTextField(
                                value = shippingAddressLine1,
                                onValueChange = { shippingAddressLine1 = it; isShippingAddressLine1Touched = true },
                                label = { Text("Line 1") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isSameShippingAndBilling,
                                isError = isShippingAddressLine1Touched && !shippingAddressLine1.matches(addressRegex)
                            )
                            if (isShippingAddressLine1Touched && !shippingAddressLine1.matches(
                                    addressRegex
                                )
                            ) {
                                Text(
                                    text = "Invalid address. Only letters, numbers, spaces, commas, periods, apostrophes, and hyphens are allowed. Minimum 5 characters.",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        item {
                            OutlinedTextField(
                                value = shippingAddressLine2,
                                onValueChange = { shippingAddressLine2 = it },
                                label = { Text("Line 2") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isSameShippingAndBilling
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = shippingCity,
                                onValueChange = { shippingCity = it; isShippingCityTouched = true },
                                label = { Text("City") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isSameShippingAndBilling,
                                isError = isShippingCityTouched && !shippingCity.matches(addressRegex)
                            )
                            if (isShippingCityTouched && !shippingCity.matches(addressRegex)) {
                                Text(
                                    text = "Invalid city. Only letters, numbers, spaces, commas, periods, apostrophes, and hyphens are allowed.",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        item {
                            OutlinedTextField(
                                value = shippingState,
                                onValueChange = { shippingState = it; isShippingStateTouched = true },
                                label = { Text("State") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isSameShippingAndBilling,
                                isError = isShippingStateTouched && !shippingState.matches(addressRegex)
                            )
                            if (isShippingStateTouched && !shippingState.matches(addressRegex)) {
                                Text(
                                    text = "Invalid state. Only letters, numbers, spaces, commas, periods, apostrophes, and hyphens are allowed.",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        item {
                            OutlinedTextField(
                                value = shippingZipCode,
                                onValueChange = { shippingZipCode = it; isShippingZipCodeTouched = true },
                                label = { Text("Zip Code") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isSameShippingAndBilling,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = isShippingZipCodeTouched && !shippingZipCode.matches(
                                    zipCodeRegex
                                )
                            )
                            if (shippingZipCode.isNotEmpty() && !shippingZipCode.matches(zipCodeRegex)) {
                                Text(
                                    text = "Invalid zip code. Enter a valid zip code.",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                } else {
                    billingAddressLine1 = "school_delivery"
                    shippingAddressLine1 = "school_delivery"
                    item {
                        OutlinedTextField(
                            value = billingAddressLine1,
                            onValueChange = { billingAddressLine1 = it; isBillingAddressLine1Touched = true },
                            label = { Text("Delivery Address") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = isBillingAddressLine1Touched && !billingAddressLine1.matches(addressRegex),
                            enabled = false
                        )
                    }
                }
            }
        }
    }
}


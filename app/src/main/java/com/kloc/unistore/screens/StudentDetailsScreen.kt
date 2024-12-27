package com.kloc.unistore.screens


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavHostController
import com.kloc.unistore.entity.student.Student
import com.kloc.unistore.firestoredb.viewmodel.EmployeeViewModel
import com.kloc.unistore.model.viewModel.MainViewModel
import com.kloc.unistore.navigation.Screen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.OutlinedTextField
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Note
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import com.kloc.unistore.common.CommonDropdownMenu
import com.kloc.unistore.common.LoadingButton
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.draw.scale


@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun StudentDetailsScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    employeeViewModel: EmployeeViewModel
) {
    val context = LocalContext.current
    val studentDetails = mainViewModel.studentViewModel.studentDetails.value
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Personal", "Contact", "Address", "Notes")
    var isLoading by remember { mutableStateOf(false) }
    var studentName by remember { mutableStateOf(studentDetails?.studentName ?: "") }
    var parentName by remember { mutableStateOf(studentDetails?.parentName ?: "") }
    var phoneNumber by remember { mutableStateOf(studentDetails?.phoneNumber ?: "") }
    var emailAddress by remember { mutableStateOf(studentDetails?.emailAddress ?: "") }
    var selectedClass by remember { mutableStateOf(studentDetails?.selectedClass ?: "") }
    var selectedGender by remember { mutableStateOf(studentDetails?.gender ?: "Male") }
    var note by remember { mutableStateOf(studentDetails?.note ?: "") }

    // Address Fields
    var billingAddressLine1 by remember { mutableStateOf(studentDetails?.billingAddressLine1 ?: "") }
    var billingAddressLine2 by remember { mutableStateOf(studentDetails?.billingAddressLine2 ?: "") }
    var billingCity by remember { mutableStateOf(studentDetails?.billingCity ?: "") }
    var billingState by remember { mutableStateOf(studentDetails?.billingState ?: "") }
    var billingZipCode by remember { mutableStateOf(studentDetails?.billingZipCode ?: "") }

    // Validation states
    var isStudentNameTouched by remember { mutableStateOf(false) }
    var isParentNameTouched by remember { mutableStateOf(false) }
    var isPhoneNumberTouched by remember { mutableStateOf(false) }
    var isEmailAddressTouched by remember { mutableStateOf(false) }
    var isClassTouched by remember { mutableStateOf(false) }
    var isBillingAddressLine1Touched by remember { mutableStateOf(false) }
    var isBillingCityTouched by remember { mutableStateOf(false) }
    var isBillingStateTouched by remember { mutableStateOf(false) }
    var isBillingZipCodeTouched by remember { mutableStateOf(false) }
    var isShippingAddressLine1Touched by remember { mutableStateOf(false) }
    var isShippingCityTouched by remember { mutableStateOf(false) }
    var isShippingStateTouched by remember { mutableStateOf(false) }
    var isShippingZipCodeTouched by remember { mutableStateOf(false) }

    // Validation regex patterns
    val nameRegex = "^[A-Za-zÀ-ÿ\\s'-.]+$".toRegex()
    val phoneNumberRegex = "^[1-9][0-9]{9}$".toRegex()
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    val zipCodeRegex = "^[0-9]{6}$".toRegex()
    val addressRegex = "^[A-Za-z0-9\\s,.'-]{5,}$".toRegex()

    // Form validation
    val isValidForm = studentName.matches(nameRegex) &&
            parentName.matches(nameRegex) &&
            phoneNumber.matches(phoneNumberRegex) &&
            emailAddress.matches(emailRegex) &&
            ((billingAddressLine1.matches(addressRegex) &&
                    billingCity.matches(addressRegex) &&
                    billingState.matches(addressRegex) &&
                    billingZipCode.matches(zipCodeRegex)) ||
            mainViewModel.schoolAddress) &&
            selectedClass.isNotBlank() &&selectedGender.isNotBlank()


    // fetching the school address
    val schoolAddress = employeeViewModel.address.value.data?.address

    LaunchedEffect(billingAddressLine1, billingAddressLine2, billingCity, billingState, billingZipCode) {
        mainViewModel.schoolAddress = billingAddressLine1 == schoolAddress?.addressLine1.toString() &&
                billingAddressLine2 == schoolAddress?.addressLine2.toString()&&
                billingCity == schoolAddress?.city.toString()&&
                billingState == schoolAddress?.state.toString()&&
                billingZipCode == schoolAddress?.zipcode.toString()
    }
    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = { if (selectedTab > 0) selectedTab-- },
                        enabled = selectedTab > 0
                    ) {
                        Icon(Icons.Rounded.ArrowBack, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Previous")
                    }

                    if (selectedTab == tabs.size - 1) {
                        Button(
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
                            },
                            enabled = isValidForm
                        ) {
                            Text("Submit",color= if (isValidForm) Color.White else Color.Black)
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.Rounded.Check, null)
                        }
                    } else {
                        Button(
                            onClick = { selectedTab++ },
                            enabled = true
                        ) {
                            Text("Next", color = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.Rounded.ArrowForward, null)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color.Black,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        // color = MaterialTheme.colorScheme.primary,
                        height = 0.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                when (index) {
                                    0 -> Icon(Icons.Rounded.Person, null, modifier = Modifier.size(16.dp))
                                    1 -> Icon(Icons.Rounded.Phone, null, modifier = Modifier.size(16.dp))
                                    2 -> Icon(Icons.Rounded.LocationOn, null, modifier = Modifier.size(16.dp))
                                    3 -> Icon(Icons.Rounded.Note, null, modifier = Modifier.size(16.dp))
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(title)
                            }
                        }
                    )
                }
            }
            LinearProgressIndicator(
                progress = (selectedTab + 1) / tabs.size.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp),
                color = Color.Black,
                trackColor = Color.White
            )

            // Content
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    slideInHorizontally { width -> width } with
                            slideOutHorizontally { width -> -width }
                }, label = ""
            ) { targetTab ->
                when (targetTab) {
                    0 -> PersonalInfoSection(
                        studentName = studentName,
                        onStudentNameChange = { studentName = it; isStudentNameTouched = true },
                        parentName = parentName,
                        onParentNameChange = { parentName = it; isParentNameTouched = true },
                        selectedGender = selectedGender,
                        onGenderChange = { selectedGender = it },
                        selectedClass = selectedClass,
                        onClassChange = { selectedClass = it; isClassTouched = true },
                        isStudentNameError = isStudentNameTouched && !studentName.matches(nameRegex),
                        isParentNameError = isParentNameTouched && !parentName.matches(nameRegex)
                    )
                    1 -> ContactInfoSection(
                        phoneNumber = phoneNumber,
                        onPhoneNumberChange = { phoneNumber = it; isPhoneNumberTouched = true },
                        emailAddress = emailAddress,
                        onEmailAddressChange = { emailAddress = it; isEmailAddressTouched = true },
                        isPhoneNumberError = isPhoneNumberTouched && !phoneNumber.matches(phoneNumberRegex),
                        isEmailError = isEmailAddressTouched && !emailAddress.matches(emailRegex)
                    )
                    2 -> AddressSection(
                        mainViewModel = mainViewModel,
                        employeeViewModel = employeeViewModel,
                        billingAddressLine1 = billingAddressLine1,
                        onBillingAddressLine1Change = { billingAddressLine1 = it
                                                      mainViewModel.typeOfAddress=false
                                                      },
                        line1Error = billingAddressLine1.isNotEmpty() && !billingAddressLine1.matches(addressRegex),
                        billingAddressLine2 = billingAddressLine2,
                        onBillingAddressLine2Change = { billingAddressLine2 = it
                            mainViewModel.typeOfAddress=false
                                                      },
                        billingCity = billingCity,
                        onBillingCityChange = { billingCity = it
                            mainViewModel.typeOfAddress=false
                                              },
                        cityError = billingCity.isNotEmpty() && !billingCity.matches(addressRegex),
                        billingState = billingState,
                        onBillingStateChange = { billingState = it
                            mainViewModel.typeOfAddress=false
                                               },
                        stateError = billingState.isNotEmpty() && !billingState.matches(addressRegex),
                        billingZipCode = billingZipCode,
                        onBillingZipCodeChange = { billingZipCode = it
                            mainViewModel.typeOfAddress=false
                                                 },
                        zipCodeError = billingZipCode.isNotEmpty() && !billingZipCode.matches(zipCodeRegex)
                    )
                    3 -> NotesSection(
                        note = note,
                        onNoteChange = { note = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun PersonalInfoSection(
    studentName: String,
    onStudentNameChange: (String) -> Unit,
    parentName: String,
    onParentNameChange: (String) -> Unit,
    selectedGender: String,
    onGenderChange: (String) -> Unit,
    selectedClass: String,
    onClassChange: (String) -> Unit,
    isStudentNameError: Boolean,
    isParentNameError: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) ,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StyledTextField(
                value = studentName,
                onValueChange = onStudentNameChange,
                label = "Student Name",
                leadingIcon = { Icon(Icons.Rounded.Person, null) },
                isError = isStudentNameError,
                errorMessage = if (isStudentNameError) "Invalid student name" else null
            )

            StyledTextField(
                value = parentName,
                onValueChange = onParentNameChange,
                label = "Parent Name",
                leadingIcon = { Icon(Icons.Rounded.People, null) },
                isError = isParentNameError,
                errorMessage = if (isParentNameError) "Invalid parent name" else null
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Box(modifier = Modifier.weight(1f)) {
                    CommonDropdownMenu(
                        label = "Gender",
                        items = listOf("Male", "Female", "Other"),
                        selectedItem = selectedGender,
                        onItemSelected = { onGenderChange(it) }
                    )
                }

//                StyledDropdown(
//                    modifier = Modifier.weight(1f),
//                    value = selectedGender,
//                    options = listOf("Male", "Female", "Other"),
//                    onValueChange = onGenderChange,
//                    label = "Gender"
//                )

                StyledTextField(
                    modifier = Modifier.weight(1f),
                    value = selectedClass,
                    onValueChange = onClassChange,
                    label = "Class",
                    leadingIcon = { Icon(Icons.Rounded.School, null) }
                )
            }
        }
    }
}

@Composable
private fun ContactInfoSection(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    emailAddress: String,
    onEmailAddressChange: (String) -> Unit,
    isPhoneNumberError: Boolean,
    isEmailError: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StyledTextField(
                value = phoneNumber,
                onValueChange = onPhoneNumberChange,
                label = "Phone Number",
                leadingIcon = { Icon(Icons.Rounded.Phone, null) },
                isError = isPhoneNumberError,
                errorMessage = if (isPhoneNumberError) "Invalid phone number" else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            StyledTextField(
                value = emailAddress,
                onValueChange = onEmailAddressChange,
                label = "Email Address",
                leadingIcon = { Icon(Icons.Rounded.Email, null) },
                isError = isEmailError,
                errorMessage = if (isEmailError) "Invalid email address" else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
        }
    }
}

@Composable
private fun AddressSection(
    mainViewModel: MainViewModel,
    employeeViewModel: EmployeeViewModel,
    billingAddressLine1: String,
    onBillingAddressLine1Change: (String) -> Unit,
    line1Error : Boolean,
    billingAddressLine2: String,
    onBillingAddressLine2Change: (String) -> Unit,
    billingCity: String,
    onBillingCityChange: (String) -> Unit,
    cityError : Boolean,
    billingState: String,
    onBillingStateChange: (String) -> Unit,
    stateError : Boolean,
    billingZipCode: String,
    onBillingZipCodeChange: (String) -> Unit,
    zipCodeError : Boolean
) {

    // fetching the school address
    val schoolAddress = employeeViewModel.address.value.data?.address

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Address",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Only school delivery!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(modifier = Modifier.scale(0.7f)) {
                        Switch(
                            checked = mainViewModel.schoolAddress,
                            onCheckedChange = {
                                mainViewModel.schoolAddress = it
                                if (mainViewModel.schoolAddress) {
                                    // Update the billing address details when the switch is enabled
                                    onBillingAddressLine1Change(schoolAddress?.addressLine1.toString())
                                    onBillingAddressLine2Change(schoolAddress?.addressLine2.toString())
                                    onBillingCityChange(schoolAddress?.city.toString())
                                    onBillingStateChange(schoolAddress?.state.toString())
                                    onBillingZipCodeChange(schoolAddress?.zipcode.toString())

                                    // Update typeOfAddress in the ViewModel
                                    mainViewModel.typeOfAddress = true
                                } else {
                                    // Optionally set it to false if the switch is disabled
                                    mainViewModel.typeOfAddress = false
                                }
                            }
                        )
                    }

                }
            }
            StyledTextField(
                value = billingAddressLine1,
                onValueChange = onBillingAddressLine1Change,
                label = "Address Line 1",
                leadingIcon = { Icon(Icons.Rounded.Home, null) },
                isError = line1Error,
                errorMessage = if (line1Error) "Invalid address. Only letters, numbers, spaces, commas, periods, apostrophes, and hyphens are allowed. Minimum 5 characters." else null,
            )
            StyledTextField(
                value = billingAddressLine2,
                onValueChange = onBillingAddressLine2Change,
                label = "Address Line 2",
                leadingIcon = { Icon(Icons.Rounded.Home, null) }
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                StyledTextField(
                    modifier = Modifier.weight(1f),
                    value = billingCity,
                    onValueChange = onBillingCityChange,
                    label = "City",
                    leadingIcon = { Icon(Icons.Rounded.LocationCity, null) },
                    isError = cityError,
                    errorMessage = if (cityError) "Invalid city. Only letters, numbers, spaces, commas, periods, apostrophes, and hyphens are allowed." else null,
                )
                StyledTextField(
                    modifier = Modifier.weight(1f),
                    value = billingState,
                    onValueChange = onBillingStateChange,
                    label = "State",
                    leadingIcon = { Icon(Icons.Rounded.Place, null) },
                    isError = stateError,
                    errorMessage = if (stateError) "Invalid state. Only letters, numbers, spaces, commas, periods, apostrophes, and hyphens are allowed." else null,
                )
            }

            StyledTextField(
                value = billingZipCode,
                onValueChange = onBillingZipCodeChange,
                label = "ZIP Code",
                leadingIcon = { Icon(Icons.Rounded.LocationOn, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = zipCodeError,
                errorMessage = if (zipCodeError)  "Invalid zip code. Enter a valid zip code." else null,
            )
            /*
            if (!mainViewModel.skipAddress) {





            } else {

            }
            */
        }
    }
}

@Composable
private fun NotesSection(
    note: String,
    onNoteChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                label = { Text("Notes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            leadingIcon = leadingIcon,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            isError = isError,
            keyboardOptions = keyboardOptions,
            singleLine = true
        )
        AnimatedVisibility(
            visible = isError && errorMessage != null,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun StyledDropdown(
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }


    OutlinedTextField(
        value = value,
        onValueChange = { },
        label = { Text(label) },
        trailingIcon = {
            Icon(
                if (expanded) Icons.Rounded.KeyboardArrowUp
                else Icons.Rounded.KeyboardArrowDown,
                null,
                Modifier.clickable { expanded = !expanded }
            ) },
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier.width(300.dp)
    ) {
        options.forEach { option ->
            DropdownMenuItem(
                text = { Text(option) },
                onClick = {
                    onValueChange(option)
                    expanded = false
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavHostController
import com.kloc.unistore.entity.student.Student
import com.kloc.unistore.firestoredb.viewmodel.EmployeeViewModel
import com.kloc.unistore.model.viewModel.MainViewModel
import com.kloc.unistore.navigation.Screen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.OutlinedTextField
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import com.kloc.unistore.common.CommonDropdownMenu
import com.kloc.unistore.common.LoadingButton
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.draw.scale

@Composable
fun StudentDetailsScreen(navController: NavHostController, mainViewModel: MainViewModel, employeeViewModel: EmployeeViewModel){
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
    var selectedClass by remember { mutableStateOf(studentDetails?.selectedClass ?: "Class1") }
    var selectedGender by remember { mutableStateOf(studentDetails?.gender ?: "Male") }
    var note by remember { mutableStateOf(studentDetails?.note ?: "Customer note") }


    // fetching the school address
    val schoolAddress = employeeViewModel.address.value.data?.address

    val nameRegex = "^[A-Za-zÀ-ÿ\\s'-.]+$".toRegex()
    val phoneNumberRegex = "^[+]?[0-9]{10,13}$".toRegex()
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    val zipCodeRegex = "^[0-9]{5,6}$".toRegex()
    val addressRegex = "^[A-Za-z0-9\\s,.'-]{5,}$".toRegex()

    val isValidForm = studentName.matches(nameRegex) && parentName.matches(nameRegex) &&
            phoneNumber.matches(phoneNumberRegex) && emailAddress.matches(emailRegex) &&
            ((billingAddressLine1.matches(addressRegex) && billingCity.matches(addressRegex) &&
                    billingState.matches(addressRegex) && billingZipCode.matches(zipCodeRegex)) || mainViewModel.schoolAddress) &&
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
    var isClassTouched by remember { mutableStateOf(false) }


    var updateSchoolAdress by remember { mutableStateOf(false) }

    LaunchedEffect(billingAddressLine1, billingAddressLine2, billingCity, billingState, billingZipCode) {
        mainViewModel.schoolAddress = billingAddressLine1 == schoolAddress?.addressLine1.toString() &&
                billingAddressLine2 == schoolAddress?.addressLine2.toString()&&
                billingCity == schoolAddress?.city.toString()&&
                billingState == schoolAddress?.state.toString()&&
                billingZipCode == schoolAddress?.zipcode.toString()
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
                            Box(modifier = Modifier.scale(0.7f)) {
                                Switch(checked = mainViewModel.schoolAddress, onCheckedChange = {
                                    mainViewModel.schoolAddress = it
                                    if (mainViewModel.schoolAddress) {
                                        billingAddressLine1 = schoolAddress?.addressLine1.toString()
                                        billingAddressLine2 = schoolAddress?.addressLine2.toString()
                                        billingCity = schoolAddress?.city.toString()
                                        billingState = schoolAddress?.state.toString()
                                        billingZipCode = schoolAddress?.zipcode.toString()
                                    }
                                    updateSchoolAdress = it
                                })
                            }

                        }
                    }
                }

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
//                if (!mainViewModel.skipAddress) {
//                    updateSchoolAdress = false
////                    if (!isSameShippingAndBilling) {
////                        item {
////                            Text(text = "Shipping Address")
////                            OutlinedTextField(
////                                value = shippingAddressLine1,
////                                onValueChange = { shippingAddressLine1 = it; isShippingAddressLine1Touched = true },
////                                label = { Text("Line 1") },
////                                modifier = Modifier.fillMaxWidth(),
////                                enabled = !isSameShippingAndBilling,
////                                isError = isShippingAddressLine1Touched && !shippingAddressLine1.matches(addressRegex)
////                            )
////                            if (isShippingAddressLine1Touched && !shippingAddressLine1.matches(
////                                    addressRegex
////                                )
////                            ) {
////                                Text(
////                                    text = "Invalid address. Only letters, numbers, spaces, commas, periods, apostrophes, and hyphens are allowed. Minimum 5 characters.",
////                                    color = MaterialTheme.colorScheme.error,
////                                    fontSize = 12.sp
////                                )
////                            }
////                        }
////                        item {
////                            OutlinedTextField(
////                                value = shippingAddressLine2,
////                                onValueChange = { shippingAddressLine2 = it },
////                                label = { Text("Line 2") },
////                                modifier = Modifier.fillMaxWidth(),
////                                enabled = !isSameShippingAndBilling
////                            )
////                        }
////                        item {
////                            OutlinedTextField(
////                                value = shippingCity,
////                                onValueChange = { shippingCity = it; isShippingCityTouched = true },
////                                label = { Text("City") },
////                                modifier = Modifier.fillMaxWidth(),
////                                enabled = !isSameShippingAndBilling,
////                                isError = isShippingCityTouched && !shippingCity.matches(addressRegex)
////                            )
////                            if (isShippingCityTouched && !shippingCity.matches(addressRegex)) {
////                                Text(
////                                    text = "Invalid city. Only letters, numbers, spaces, commas, periods, apostrophes, and hyphens are allowed.",
////                                    color = MaterialTheme.colorScheme.error,
////                                    fontSize = 12.sp
////                                )
////                            }
////                        }
////                        item {
////                            OutlinedTextField(
////                                value = shippingState,
////                                onValueChange = { shippingState = it; isShippingStateTouched = true },
////                                label = { Text("State") },
////                                modifier = Modifier.fillMaxWidth(),
////                                enabled = !isSameShippingAndBilling,
////                                isError = isShippingStateTouched && !shippingState.matches(addressRegex)
////                            )
////                            if (isShippingStateTouched && !shippingState.matches(addressRegex)) {
////                                Text(
////                                    text = "Invalid state. Only letters, numbers, spaces, commas, periods, apostrophes, and hyphens are allowed.",
////                                    color = MaterialTheme.colorScheme.error,
////                                    fontSize = 12.sp
////                                )
////                            }
////                        }
////                        item {
////                            OutlinedTextField(
////                                value = shippingZipCode,
////                                onValueChange = { shippingZipCode = it; isShippingZipCodeTouched = true },
////                                label = { Text("Zip Code") },
////                                modifier = Modifier.fillMaxWidth(),
////                                enabled = !isSameShippingAndBilling,
////                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
////                                isError = isShippingZipCodeTouched && !shippingZipCode.matches(
////                                    zipCodeRegex
////                                )
////                            )
////                            if (shippingZipCode.isNotEmpty() && !shippingZipCode.matches(zipCodeRegex)) {
////                                Text(
////                                    text = "Invalid zip code. Enter a valid zip code.",
////                                    color = MaterialTheme.colorScheme.error,
////                                    fontSize = 12.sp
////                                )
////                            }
////                        }
////                    }
//                }
//                else {
////                    if (updateSchoolAdress) {
////                        billingAddressLine1 = schoolAddress?.addressLine1.toString()
////                        billingAddressLine2 = schoolAddress?.addressLine2.toString()
////                        billingCity = schoolAddress?.city.toString()
////                        billingState = schoolAddress?.state.toString()
////                        billingZipCode = schoolAddress?.zipcode.toString()
////                    }
////                    updateSchoolAdress = false
////                    isSameShippingAndBilling=true
//                }
            }
        }
    }
}



*/
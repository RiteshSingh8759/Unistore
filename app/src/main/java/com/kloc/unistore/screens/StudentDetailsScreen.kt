package com.kloc.unistore.screens

import com.dokar.sonner.*
import android.annotation.SuppressLint
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.*
import androidx.compose.animation.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavHostController
import com.kloc.unistore.entity.student.Student
import com.kloc.unistore.firestoredb.viewmodel.EmployeeViewModel
import com.kloc.unistore.model.viewModel.MainViewModel
import com.kloc.unistore.navigation.Screen
import com.kloc.unistore.common.CommonDropdownMenu
import kotlin.time.Duration.Companion.milliseconds

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun StudentDetailsScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    employeeViewModel: EmployeeViewModel
) {
    var isLoading by remember { mutableStateOf(false) }

    // Student details
    val studentDetails = mainViewModel.studentViewModel.studentDetails.value
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

    // Regex for field validation
    val nameRegex = ("^\\s*[A-Za-z\u0900-\u097F][A-Za-z\u0900-\u097F.''\\s-]{1,48}[A-Za-z\u0900-\u097F]\\s*$").toRegex()
    val phoneNumberRegex = "^\\s*(?!.*(\\d)\\1{7})[6-9][0-9]{9}\\s*$".toRegex()
    val emailRegex = ("^\\s*[a-zA-Z0-9](?:[a-zA-Z0-9._-]{0,61}[a-zA-Z0-9])?@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.(?:[a-zA-Z]{2,8})\\s*$").toRegex()
    val addressLineRegex = ("^\\s*[a-zA-Z0-9](?:[a-zA-Z0-9\\s,./\\-_()]{3,298}[a-zA-Z0-9])\\s*$").toRegex()
    val cityRegex = ("^\\s*[A-Za-z][A-Za-z\\s-]{0,48}[A-Za-z]\\s*$").toRegex()
    val stateRegex = ("^\\s*(?i)(Andhra Pradesh|Arunachal Pradesh|Assam|Bihar|Chhattisgarh|Goa|Gujarat|Haryana|Himachal Pradesh|Jharkhand|Karnataka|Kerala|Madhya Pradesh|Maharashtra|Manipur|Meghalaya|Mizoram|Nagaland|Odisha|Punjab|Rajasthan|Sikkim|Tamil Nadu|Telangana|Tripura|Uttar Pradesh|Uttarakhand|West Bengal|Andaman and Nicobar Islands|Chandigarh|Dadra and Nagar Haveli and Daman and Diu|Delhi|Jammu and Kashmir|Ladakh|Lakshadweep|Puducherry)\\s*$").toRegex()
    val zipCodeRegex = ("^\\s*[1-9][0-9]{5}\\s*$").toRegex()

    // Form validation for required fields
    val isValidForm =
        studentName.matches(nameRegex) &&
        parentName.matches(nameRegex) &&
        phoneNumber.matches(phoneNumberRegex) &&
        emailAddress.matches(emailRegex) &&
        ((billingAddressLine1.matches(addressLineRegex) &&
         (billingAddressLine2.isEmpty() || billingAddressLine2.matches(addressLineRegex)) &&
          billingCity.matches(cityRegex) &&
          billingState.matches(stateRegex) &&
          billingZipCode.matches(zipCodeRegex)) ||
         mainViewModel.schoolAddress) &&
        selectedClass.isNotBlank() &&
        selectedGender.isNotBlank()


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
            Button(
                onClick = {
                    isLoading = true
                    val newStudent = Student(
                        studentName = studentName.trim(),
                        parentName = parentName.trim(),
                        phoneNumber = phoneNumber.trim(),
                        emailAddress = emailAddress.trim(),
                        billingAddressLine1 = billingAddressLine1.trim(),
                        billingAddressLine2 = billingAddressLine2.trim(),
                        billingCity = billingCity.trim(),
                        billingState = billingState.trim(),
                        billingZipCode = billingZipCode.trim(),
                        selectedClass = selectedClass.trim(),
                        gender = selectedGender.trim(),
                        note = note.trim()
                    )
                    mainViewModel.studentViewModel.saveStudentDetails(newStudent)
                    isLoading = false
                    navController.navigate(Screen.OrderDetailsScreen.route) {
                        popUpTo(Screen.StudentDetailsScreen.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                enabled = isValidForm
            ) {
                Text("Submit",color= if (isValidForm) Color.White else Color.Black)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Rounded.Check, null)
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().background(Color.White).padding(padding)) {
            item {
                PersonalInfoSection(
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
            }
            item {
                ContactInfoSection(
                    phoneNumber = phoneNumber,
                    onPhoneNumberChange = { phoneNumber = it; isPhoneNumberTouched = true },
                    emailAddress = emailAddress,
                    onEmailAddressChange = { emailAddress = it; isEmailAddressTouched = true },
                    isPhoneNumberError = isPhoneNumberTouched && !phoneNumber.matches(phoneNumberRegex),
                    isEmailError = isEmailAddressTouched && !emailAddress.matches(emailRegex)
                )
            }
            item {
                AddressSection(
                    mainViewModel = mainViewModel,
                    employeeViewModel = employeeViewModel,
                    billingAddressLine1 = billingAddressLine1,
                    onBillingAddressLine1Change = { billingAddressLine1 = it; mainViewModel.typeOfAddress = false },
                    line1Error = billingAddressLine1.isNotEmpty() && !billingAddressLine1.matches(addressLineRegex),
                    billingAddressLine2 = billingAddressLine2,
                    onBillingAddressLine2Change = { billingAddressLine2 = it; mainViewModel.typeOfAddress=false },
                    line2Error = billingAddressLine2.isNotEmpty() && !billingAddressLine2.matches(addressLineRegex),
                    billingCity = billingCity,
                    onBillingCityChange = { billingCity = it; mainViewModel.typeOfAddress=false },
                    cityError = billingCity.isNotEmpty() && !billingCity.matches(cityRegex),
                    billingState = billingState,
                    onBillingStateChange = { billingState = it; mainViewModel.typeOfAddress=false },
                    billingZipCode = billingZipCode,
                    onBillingZipCodeChange = { billingZipCode = it; mainViewModel.typeOfAddress=false },
                    zipCodeError = billingZipCode.isNotEmpty() && !billingZipCode.matches(zipCodeRegex)
                )
            }
            item { NotesSection(note = note, onNoteChange = { note = it }) }
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
        modifier = Modifier.fillMaxWidth().padding(16.dp).animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) ,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            StyledTextField(
                value = studentName,
                onValueChange = onStudentNameChange,
                label = "Student Name*",
                leadingIcon = { Icon(Icons.Rounded.Person, null) },
                isError = isStudentNameError,
                errorMessage = if (isStudentNameError) nameValidationMessage(studentName.trim()) else null,
                maxLength = 50
            )

            StyledTextField(
                value = parentName,
                onValueChange = onParentNameChange,
                label = "Parent Name*",
                leadingIcon = { Icon(Icons.Rounded.People, null) },
                isError = isParentNameError,
                errorMessage = if (isParentNameError) nameValidationMessage(parentName.trim()) else null,
                maxLength = 50
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    CommonDropdownMenu(
                        label = "Gender*",
                        items = listOf("Male", "Female"),
                        selectedItem = selectedGender,
                        onItemSelected = { onGenderChange(it) },
                        leadingIcon = { Icon(if (selectedGender == "Male") Icons.Rounded.Male else Icons.Rounded.Female, null)  }
                    )
                }
                StyledTextField(
                    modifier = Modifier.weight(1f),
                    value = selectedClass,
                    onValueChange = onClassChange,
                    label = "Class*",
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
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            StyledTextField(
                value = phoneNumber,
                onValueChange = onPhoneNumberChange,
                label = "Phone Number*",
                leadingIcon = { Icon(Icons.Rounded.Phone, null) },
                isError = isPhoneNumberError,
                errorMessage = if (isPhoneNumberError) phoneNumberValidationMessage(phoneNumber.trim()) else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                maxLength = 10
            )
            StyledTextField(
                value = emailAddress,
                onValueChange = onEmailAddressChange,
                label = "Email Address*",
                leadingIcon = { Icon(Icons.Rounded.Email, null) },
                isError = isEmailError,
                errorMessage = if (isEmailError) emailValidationMessage(emailAddress.trim()) else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                maxLength = 254
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
    line2Error : Boolean,
    billingCity: String,
    onBillingCityChange: (String) -> Unit,
    cityError : Boolean,
    billingState: String,
    onBillingStateChange: (String) -> Unit,
    billingZipCode: String,
    onBillingZipCodeChange: (String) -> Unit,
    zipCodeError : Boolean
) {

    val toaster = rememberToasterState()
    Toaster(state = toaster, darkTheme = true, maxVisibleToasts = 1)
    // fetching the school address
    val schoolAddress = employeeViewModel.address.value.data?.address

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
                    Text(text = "Only school delivery!", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(modifier = Modifier.scale(0.7f)) {
                        Switch(
                            checked = mainViewModel.schoolAddress,
                            onCheckedChange = {
                                mainViewModel.schoolAddress = it
                                if (mainViewModel.schoolAddress) {
                                    val addressLine1 = schoolAddress?.addressLine1
                                    val addressLine2 = schoolAddress?.addressLine2
                                    val city = schoolAddress?.city
                                    val state = schoolAddress?.state
                                    val zipcode = schoolAddress?.zipcode

                                    if (addressLine1.isNullOrEmpty() || addressLine2.isNullOrEmpty() || city.isNullOrEmpty() || state.isNullOrEmpty() || zipcode.isNullOrEmpty()) {
                                        mainViewModel.schoolAddress = false
                                        mainViewModel.typeOfAddress = false
                                        toaster.show(Toast(message = "School details not found. Please provide complete details.", type = ToastType.Error, duration = 2000.milliseconds))
                                    } else {
                                        onBillingAddressLine1Change(addressLine1)
                                        onBillingAddressLine2Change(addressLine2)
                                        onBillingCityChange(city)
                                        onBillingStateChange(state)
                                        onBillingZipCodeChange(zipcode)
                                        mainViewModel.typeOfAddress = true
                                    }
                                }  else {
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
                label = "Address Line 1*",
                leadingIcon = { Icon(Icons.Rounded.Home, null) },
                isError = line1Error,
                errorMessage = if (line1Error) addressValidationMessage(billingAddressLine1.trim()) else null,
                maxLength = 300
            )
            StyledTextField(
                value = billingAddressLine2,
                onValueChange = onBillingAddressLine2Change,
                label = "Address Line 2",
                leadingIcon = { Icon(Icons.Rounded.Home, null) },
                isError = line2Error,
                errorMessage = if (line2Error) addressValidationMessage(billingAddressLine2.trim()) else null,
                maxLength = 300
            )
            StyledTextField(
                value = billingCity,
                onValueChange = onBillingCityChange,
                label = "City*",
                leadingIcon = { Icon(Icons.Rounded.LocationCity, null) },
                isError = cityError,
                errorMessage = if (cityError) cityValidationMessage(billingCity.trim()) else null,
                maxLength = 50
            )
            CommonDropdownMenu(
                label = "State*",
                items = listOf(
                    "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh",
                    "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka",
                    "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram",
                    "Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu",
                    "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal",
                    "Andaman and Nicobar Islands", "Chandigarh", "Dadra and Nagar Haveli and Daman and Diu",
                    "Delhi", "Jammu and Kashmir", "Ladakh", "Lakshadweep", "Puducherry"
                ),
                selectedItem = billingState,
                onItemSelected = { onBillingStateChange(it) },
                leadingIcon = { Icon(Icons.Rounded.Place, null) }
            )
            StyledTextField(
                value = billingZipCode,
                onValueChange = onBillingZipCodeChange,
                label = "ZIP Code*",
                leadingIcon = { Icon(Icons.Rounded.LocationOn, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = zipCodeError,
                errorMessage = if (zipCodeError)  zipCodeValidationMessage(billingZipCode.trim()) else null,
                maxLength = 6
            )
        }
    }
}


@Composable
private fun NotesSection(note: String, onNoteChange: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth().height(200.dp),
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
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    maxLength: Int? = null
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { if (maxLength == null || it.length <= maxLength) { onValueChange(it) } },
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

fun nameValidationMessage(value: String): String? {

    if (value.isBlank()) { return "⚠️ Name cannot be empty" }

    if (!Regex("^[A-Za-z\u0900-\u097F]").matches(value.first().toString())) { return "⚠️ Name cannot start with '${if (value.first() != ' ') value.first() else "space"}'" }
    if (!Regex("^[A-Za-z\u0900-\u097F]").matches(value.last().toString())) { return "⚠️ Name cannot end with '${if (value.last() != ' ') value.last() else "space"}'" }

    value.forEach { char -> if (!Regex("^[A-Za-z\u0900-\u097F.'\\s-]").matches(char.toString())) { return "⚠️ Name contains an invalid character: '$char'" } }

    if (value.length < 3) { return "⚠️ Name must be at least 3 characters long" }
    if (value.length > 50) { return "⚠️ Name cannot exceed 50 characters" }

    return null
}
fun phoneNumberValidationMessage(value: String): String? {

    if (value.isBlank()) { return "⚠️ Phone number cannot be empty" }
    if (!value.all { it.isDigit() }) { return "⚠️ Phone number must contain digits only" }
    if (value.first() !in "6789") { return "⚠️ Phone number must start with 6, 7, 8, or 9" }
    if (value.length != 10) { return "⚠️ Phone number must be exactly 10 digits long" }
    return null
}
fun emailValidationMessage(value: String): String? {
    if (value.isBlank()) { return "⚠️ Email cannot be empty" }
    if (!value.first().isLetterOrDigit()) { return "⚠️ Email must start with a letter or number." }
    if (value.count { it == '@' } > 1) { return "⚠️ Email must contain exactly one '@' symbol." }

    val parts = value.split("@")
    if (parts.size == 2) {
        val localPart = parts[0]
        val domainPart = parts[1]
        if (localPart.isEmpty()) { return "⚠️ Username must be at least 1 characters long." }
        if (!localPart.first().isLetterOrDigit()) { return "⚠️ Username must start with a letter or number." }
        for (char in localPart) { if (!char.isLetterOrDigit() && char !in ".%+-_") { return "⚠️ Invalid character '${if (char != ' ') char else "space"}' in username." } }
        if (!localPart.last().isLetterOrDigit()) { return "⚠️ Username must end with a letter or number." }
        if (localPart.length > 64) { return "⚠️ Username cannot exceed 64 characters." }
        if (domainPart.isEmpty()) { return "⚠️ Domain must be at least 1 characters long." }
        if (!domainPart.first().isLetterOrDigit()) { return "⚠️ Domain must start with a letter or number." }
        for (char in domainPart) { if (!char.isLetterOrDigit() && char !in ".-") { return "⚠️ Invalid character '${if (char != ' ') char else "space"}' in domain." } }
        if (!domainPart.last().isLetterOrDigit()) { return "⚠️ Domain must end with a letter or number." }
        if (domainPart.length > 253) { return "⚠️ Domain cannot exceed 253 characters." }
        if (domainPart.count { it == '.' } > 1) { return "⚠️ Domain must contain exactly one '.' symbol." }
        val tld = domainPart.substringAfterLast(".")
        if (localPart.length < 2) { return "⚠️ Top-level domain must be at least 2 characters long." }
        if (localPart.length > 8) { return "⚠️ Top-level domain cannot exceed 8 characters." }
        for (char in tld) { if (!char.isLetter()) { return "⚠️ Top-level domain only contains letter." } }
    }

    value.forEach { char -> if (!char.isLetterOrDigit() && char !in "._%+-@") { return "⚠️ Email contains an invalid character: '${if (char != ' ') char else "space"}'" } }

    if (value.length < 6) { return "⚠️ Email address must be at least 6 characters long." }
    if (value.length > 254) { return "⚠️ Email address cannot exceed 254 characters." }

    return null
}
fun addressValidationMessage(value: String): String? {

    val stack = mutableListOf<Char>()
    var containsEmptyParentheses = false

    if (value.isBlank()) { return "⚠️ Address cannot be empty" }
    if (!value.first().let { it.isLetterOrDigit() || it == '(' }) { return "⚠️ Address cannot start with '${if (value.first() != ' ') value.first() else "space"}'" }
    if (!value.last().let { it.isLetterOrDigit() || it == ')' || it == '.' }) { return "⚠️ Address cannot end with '${if (value.last() != ' ') value.last() else "space"}'" }
    if (value.count { it == '(' } != value.count { it == ')' }) { return "⚠️ Address has unbalanced parentheses" }

    value.forEachIndexed { index, char ->
        when (char) {
            '(' -> stack.add(char)
            ')' -> {
                if (stack.isEmpty()) return "⚠️ Address has an extra closing parenthesis"
                if (index > 0 && value[index - 1] == '(') containsEmptyParentheses = true
                stack.removeAt(stack.size - 1)
            }
            else -> if (!char.isLetterOrDigit() && char !in ",./() ")
                return "⚠️ Address contains an invalid character: '${if (char != ' ') char else "space"}'"
        }
    }

    if (containsEmptyParentheses) { return "⚠️ Address contains empty parentheses '()'" }
    if (stack.isNotEmpty()) { return "⚠️ Address has an extra opening parenthesis" }
    if (value.length < 5) { return "⚠️ Address must be at least 5 characters long" }
    if (value.length > 300) { return "⚠️ Address cannot exceed 300 characters" }

    // Detect consecutive special characters
    val match = Regex("([,./()-])\\1+").find(value)
    if (match != null) { return "⚠️ Address contains consecutive special characters: '${match.value}'" }

    return null
}
fun cityValidationMessage(value: String): String? {

    if (value.isBlank()) { return "⚠️ City cannot be empty" }
    if (!value.first().isLetter()) { return "⚠️ City should be start with letter" }
    if (!value.last().isLetter()) { return "⚠️ City should be end with letter" }
    value.firstOrNull { !it.isLetter() && it != ' ' }?.let { return "⚠️ City contains an invalid character: '${if (it != ' ') it else "space"}'" }
    if (value.length < 2) { return "⚠️ City must be at least 2 characters long." }
    if (value.length > 50) { return "⚠️ City cannot exceed 50 characters." }

    return null
}
fun zipCodeValidationMessage(value: String): String? {

    if (value.isBlank()) { return "⚠️ Zip code cannot be empty" }
    if (value.first() == '0') { return "⚠️ Zip code cannot start with 0" }
    if (!value.all { it.isDigit() }) { return "⚠️ Zip code should be number" }
    if (value.length != 6) { return "⚠️ Zip code must be exactly 6 digits long" }

    return null
}
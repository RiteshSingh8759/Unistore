package com.kloc.unistore.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import com.dokar.sonner.ToastType
import com.kloc.unistore.model.viewModel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dokar.sonner.Toast
import com.dokar.sonner.Toaster
import com.dokar.sonner.rememberToasterState
import com.kloc.unistore.common.LoadingButton
import com.kloc.unistore.firestoredb.viewmodel.EmployeeViewModel
import com.kloc.unistore.mail.EmailSender
import com.kloc.unistore.navigation.Screen
import kotlinx.coroutines.Dispatchers
import javax.mail.MessagingException
import kotlin.time.Duration.Companion.milliseconds



@Composable
fun SignInScreen(navController: NavController, employeeViewModel: EmployeeViewModel, mainViewModel: MainViewModel) {

    val toaster = rememberToasterState()
    Toaster(state = toaster, darkTheme = true, maxVisibleToasts = 1)

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val parts = listOf(
        "More than a Uniform......A Sense of Style  🔥",
        "Uniforms So Sharp, They'll Make Your Wardrobe Do a Double-Take! 👀",
        "Dress to Impress with UniStore Uniforms! 💯"
    )
    var partIndex by remember { mutableIntStateOf(0) }
    var partText by remember { mutableStateOf("") }

    // Advanced typewriter effect
    LaunchedEffect(key1 = parts) {
        while (true) {
            val part = parts[partIndex]
            part.forEachIndexed { charIndex, _ ->
                partText = part.substring(startIndex = 0, endIndex = charIndex + 1)
                delay(100)
            }
            delay(1000)
            part.forEachIndexed { charIndex, _ ->
                partText = part.substring(
                    startIndex = 0,
                    endIndex = part.length - (charIndex + 1)
                )
                delay(30)
            }
            delay(500)
            partIndex = (partIndex + 1) % parts.size
        }
    }

    var staff_id by remember { mutableStateOf("") }
    var generatedOtp by remember { mutableStateOf("") }
    var userInputOtp by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var staffError by remember { mutableStateOf("") }
    var staffNotRegistered by remember { mutableStateOf(false) }
    var isSentOtpClicked by remember { mutableStateOf(false) }
    var resendEnabled by remember { mutableStateOf(false) }
    var countdown by remember { mutableIntStateOf(30) }
    var isVerifyingOtp by remember { mutableStateOf(false) }
    var otpError by remember { mutableStateOf(false) }

    val userData = employeeViewModel.res1.value

    LaunchedEffect(staff_id) {
        if (staff_id.isNotEmpty()) {
            employeeViewModel.getUserByEmail(staff_id.trim().uppercase())
        }
    }

    LaunchedEffect(userData) {
        staffNotRegistered = userData.data == null
    }


    if (otpSent) {
        // Countdown timer for OTP resend
        LaunchedEffect(otpSent) {
            countdown = 30
            resendEnabled = false
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
            resendEnabled = true
        }
        // Go back to send otp box
        LaunchedEffect(otpSent) {
            delay(60000)
            generatedOtp = ""
            otpSent = !otpSent
        }
    }

    // Automatically validate OTP when complete
    LaunchedEffect(userInputOtp) {
        if (userInputOtp.length == 6 && !isVerifyingOtp) {
            isVerifyingOtp = true
            if (userInputOtp == generatedOtp) {
                otpError = false
                toaster.show(Toast(message = "OTP Verified!", type = ToastType.Success, duration = 2000.milliseconds))
                navController.navigate(route = Screen.SchoolDetailsScreen.route)
            } else {
                otpError = true
                userInputOtp = ""
            }
            isVerifyingOtp = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // White background box
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.8f))
        )

        // Main content Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = buildAnnotatedString {
                        if (partText.isNotEmpty()) {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(partText.first().uppercase())
                            }
                            append(partText.substring(1))
                        } else {
                            append(partText)
                        }
                    },
                    color = Color.Black,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    softWrap = true
                )
            }

            Spacer(modifier = Modifier.height(126.dp))

            Text(
                "Sign In",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = staff_id,
                onValueChange = {
                    if (!otpSent) {
                        staff_id = it
                        staffError = ""
                    }
                },
                label = { Text("Enter Staff Id") },
                isError = staffNotRegistered || staffError.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                enabled = !otpSent
            )

            if (staffNotRegistered && staff_id.isNotEmpty()) {
                Text(
                    text = "Staff ID is not registered to Unistore.",
                    color = Color.Red,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            if (staffError.isNotEmpty() && staff_id.isNotEmpty()) {
                Text(
                    text = staffError,
                    color = Color.Red,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!otpSent) {
                LoadingButton(
                    text = "Send OTP",
                    isLoading = !otpSent && isSentOtpClicked,
                    isEnabled = staff_id.isNotBlank() && !(staffNotRegistered && staff_id.isNotEmpty()),
                    onClick = {
                        isSentOtpClicked = true
                        if (staff_id.isNotEmpty()) {
                            coroutineScope.launch(Dispatchers.IO) {
                                try {
                                    val otp = (100000..999999).random().toString()
                                    EmailSender.sendEmail(
                                        toEmail = userData.data?.employee?.email,
                                        subject = "Your OTP Code",
                                        body = "Your OTP is: $otp",
                                        fromEmail = "ritesh.singh@kloctechnologies.com",
                                        password = "xjwfxiseksaouxue"
                                    )
                                    withContext(Dispatchers.Main) {
                                        toaster.show(Toast(message =  "OTP Sent to your email.", type = ToastType.Success, duration = 2000.milliseconds))
                                        otpSent = true
                                        generatedOtp = otp
                                    }
                                } catch (e: MessagingException) {
                                    withContext(Dispatchers.Main) {
                                        toaster.show(Toast(message =  "Error sending OTP. Try again.", type = ToastType.Error, duration = 2000.milliseconds))
                                    }
                                } finally {
                                    isSentOtpClicked = false
                                }
                            }
                        } else {
                            staffError = "Please enter a valid staff id."
                        }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                Text(
                    text = "Enter verification code",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                OtpInput(
                    otpText = userInputOtp,
                    onOtpTextChange = {
                        userInputOtp = it
                        otpError = false  // Reset error when user starts typing again
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                if (otpError) {
                    Text(
                        text = "Entered wrong OTP. Try again.",
                        color = Color.Red,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!resendEnabled) {
                    Text(
                        text = "Resend OTP in $countdown seconds",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                } else {
                    Text(
                        text = "Didn't receive the code? Go Back",
                        modifier = Modifier.clickable {
                            userInputOtp = ""
                            otpSent = false
                        },
                        color = Color(0xFFF10555),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
        BottomBar(modifier = Modifier.align(Alignment.BottomCenter))
    }
}


@Composable
fun BottomBar(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Box(modifier = modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Bottom
        ) {
            // Phone Number
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:8217347259")
                    }
                    context.startActivity(intent)
                }
            ) {
                Icon(imageVector = Icons.Default.Phone, contentDescription = "Phone", tint = Color.Black)
                Text(text = "8217347259", fontSize = 15.sp, color = Color.Black)
            }
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:uniformunipro@gmail.com")
                    }
                    context.startActivity(intent)
                }
            ) {
                Icon(imageVector = Icons.Default.Email, contentDescription = "Email", tint = Color.Black)
                Text(text = "uniformunipro@gmail.com", fontSize = 15.sp, color = Color.Black)
            }

        }
    }
}

@Composable
fun OtpInput(
    modifier: Modifier = Modifier,
    otpText: String,
    otpCount: Int = 6,
    onOtpTextChange: (String) -> Unit
) {
    val focusRequesters = remember { List(otpCount) { FocusRequester() } }

    BasicTextField(
        modifier = modifier,
        value = TextFieldValue(otpText, selection = TextRange(otpText.length)),
        onValueChange = { newValue ->
            val newText = newValue.text
            if (newText.length <= otpCount) {
                if (newText.length < otpText.length) {
                    // Backspace was pressed
                    focusRequesters.getOrNull(newText.length - 1)?.requestFocus()
                } else if (newText.length > otpText.length) {
                    // New character was added
                    focusRequesters.getOrNull(newText.length)?.requestFocus()
                }
                onOtpTextChange(newText)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                repeat(otpCount) { index ->
                    Box(modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .focusRequester(focusRequesters[index])
                        .border(
                            3.dp, if (index == otpText.length) Color(
                                0xFF0E0D0F
                            ) else Color.Gray, RoundedCornerShape(8.dp)
                        )
                        .padding(2.dp)) {
                        Text(
                            text = when {
                                index == otpText.length -> ""
                                index > otpText.length -> ""
                                else -> otpText[index].toString()
                            },
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    )
    LaunchedEffect(Unit) { focusRequesters.firstOrNull()?.requestFocus() }
}


/*
@Composable
fun SignInScreen(navController: NavController, employeeViewModel: EmployeeViewModel, mainViewModel: MainViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Combined state management
    data class SignInState(
        val showToast: Boolean = false,
        val toastData: Pair<String, ToastType>? = null,
        val staffId: String = "",
        val userInputOtp: String = "",
        val otpSent: Boolean = false,
        val isSentOtpClicked: Boolean = false,
        val staffError: String = "",
        val staffNotRegistered: Boolean = false,
        val generatedOtp: String = "",
        val resendTimer: Int = 10,
        val canResend: Boolean = false,
        val wrongOtp: Boolean = false,
    )
    var state by remember { mutableStateOf(SignInState()) }
    val userData = employeeViewModel.res1.value

    // Typewriter effect management
    val messages = listOf(
        "More than a Uniform......A Sense of Style  🔥",
        "Uniforms So Sharp, They'll Make Your Wardrobe Do a Double-Take! 👀",
        "Dress to Impress with UniStore Uniforms! 💯"
    )
    var displayText by remember { mutableStateOf("") }

    LaunchedEffect(!state.canResend) {
        while (state.resendTimer > 0) {
            delay(1000)
            state = state.copy(resendTimer = state.resendTimer-1)
        }
        state = state.copy(resendTimer = 10, canResend = true)
    }
    LaunchedEffect(Unit) {
        var messageIndex = 0
        while (true) {
            val message = messages[messageIndex]
            // Type in
            for (i in message.indices) {
                displayText = message.substring(0, i + 1)
                delay(100)
            }
            delay(1000)
            // Type out
            for (i in message.indices.reversed()) {
                displayText = message.substring(0, i)
                delay(30)
            }
            delay(500)
            messageIndex = (messageIndex + 1) % messages.size
        }
    }

    // Staff ID validation effect
    LaunchedEffect(state.staffId) {
        if (state.staffId.isNotEmpty()) {
            employeeViewModel.getUserByEmail(state.staffId.trim().uppercase())
        }
    }

    LaunchedEffect(userData) {
        state = state.copy(staffNotRegistered = userData.data == null)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = 0.8f)))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Typewriter text display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = buildAnnotatedString {
                        if (displayText.isNotEmpty()) {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(displayText.first().uppercase())
                            }
                            append(displayText.substring(1))
                        }
                    },
                    color = Color.Black,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    softWrap = true
                )
            }

            Spacer(modifier = Modifier.height(126.dp))
            Text("Sign In", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            // OTP handling
            if (!state.otpSent) {

                // Staff ID input field
                OutlinedTextField(
                    value = state.staffId,
                    onValueChange = { state = state.copy(staffId = it, staffError = "") },
                    label = { Text("Enter Staff Id") },
                    isError = state.staffNotRegistered || state.staffError.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )

                // Error messages
                if (state.staffNotRegistered && state.staffId.isNotEmpty()) {
                    Text(
                        "Staff ID is not registered to Unistore.",
                        color = Color.Red,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                if (state.staffError.isNotEmpty() && state.staffId.isNotEmpty()) {
                    Text(
                        state.staffError,
                        color = Color.Red,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LoadingButton(
                    text = "Send OTP",
                    isLoading = !state.otpSent && state.isSentOtpClicked,
                    isEnabled =  state.staffId.isNotEmpty() && !(state.staffNotRegistered && state.staffId.isNotEmpty()),
                    onClick = {
                        state = state.copy(isSentOtpClicked = true)
                        if (state.staffId.isNotEmpty()) {
                            coroutineScope.launch(Dispatchers.IO) {
                                try {
                                    val otp = (100000..999999).random().toString()
                                    EmailSender.sendEmail(
                                        toEmail = userData.data?.employee?.email,
                                        subject = "Your OTP Code",
                                        body = "Your OTP is: $otp",
                                        fromEmail = "ritesh.singh@kloctechnologies.com",
                                        password = "xjwfxiseksaouxue"
                                    )
                                    withContext(Dispatchers.Main) {
                                        state = state.copy(
                                            toastData = "OTP Sent to your email." to ToastType.Success,
                                            showToast = true,
                                            otpSent = true,
                                            generatedOtp = otp
                                        )
                                    }
                                } catch (e: MessagingException) {
                                    withContext(Dispatchers.Main) {
                                        state = state.copy(
                                            toastData = "Error sending OTP. Try again." to ToastType.Error,
                                            showToast = true
                                        )
                                    }
                                } finally {
                                    state = state.copy(isSentOtpClicked = false)
                                }
                            }
                        } else {
                            state = state.copy(staffError = "Please enter a valid staff id.")
                        }
                    }
                )
            } else {
                OutlinedTextField(
                    value = state.userInputOtp,
                    onValueChange = { state = state.copy(userInputOtp = it) },
                    label = { Text("Enter OTP") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                LoadingButton(text = "Verify OTP", isLoading = false, isEnabled = true, onClick = {
                    if (state.userInputOtp == state.generatedOtp) {
                        state = state.copy(
                            toastData = "OTP Verified!" to ToastType.Success,
                            showToast = true
                        )
                        navController.navigate(Screen.SchoolDetailsScreen.route)
                    } else {
                        state = state.copy(
                            toastData = "Invalid OTP. Try again." to ToastType.Warning,
                            showToast = true
                        )
                    }
                })

            }

            if (state.showToast) {
                state.toastData?.let {
                    CommonToast(message = it.first, type = it.second) {
                        state = state.copy(showToast = false)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
        BottomBar(modifier = Modifier.align(Alignment.BottomCenter))
    }
}


@Composable
fun BottomBar(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Box(modifier = modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Bottom
        ) {
            // Phone Number
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:8217347259")
                    }
                    context.startActivity(intent)
                }
            ) {
                Icon(imageVector = Icons.Default.Phone, contentDescription = "Phone", tint = Color.Black)
                Text(text = "8217347259", fontSize = 15.sp, color = Color.Black)
            }
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:uniformunipro@gmail.com")
                    }
                    context.startActivity(intent)
                }
            ) {
                Icon(imageVector = Icons.Default.Email, contentDescription = "Email", tint = Color.Black)
                Text(text = "uniformunipro@gmail.com", fontSize = 15.sp, color = Color.Black)
            }

        }
    }
}
*/
package com.kloc.unistore.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.dokar.sonner.ToastType
import com.dokar.sonner.*
import com.dokar.sonner.rememberToasterState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.kloc.unistore.R
import com.kloc.unistore.common.LoadingButton
import com.kloc.unistore.firestoredb.module.DeviceModel
import com.kloc.unistore.firestoredb.viewmodel.EmployeeViewModel
import com.kloc.unistore.model.schoolViewModel.SchoolViewModel
import com.kloc.unistore.model.viewModel.MainViewModel
import com.kloc.unistore.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SchoolDetailsScreen(
    navController: NavHostController,
    employeeViewModel: EmployeeViewModel,
    viewModel: SchoolViewModel = hiltViewModel()
) {
    val toaster = rememberToasterState()
    Toaster(state = toaster, darkTheme = true, maxVisibleToasts = 1)

    var slugId by remember { mutableStateOf("") }
    // AJ : Removed Toaster
    val schoolDetails by viewModel.schoolDetails.collectAsState(initial = null)
    var isLoading by remember { mutableStateOf(false) }
    var lastClickTime by remember { mutableLongStateOf(0L) }

    //staff related
    val userData = employeeViewModel.res1.value
    val deviceListState = employeeViewModel.deviceList.value
    var selectedDevice by remember { mutableStateOf<DeviceModel?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var userInput = employeeViewModel.userInput.value
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Carousel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )
        Text(
            text = "Welcome ${userData.data?.employee?.name ?: "User"}",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium
        )
        OutlinedTextField(
            value = slugId,
            onValueChange = { slugId = it },
            label = { Text("Enter School Code") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        // code for dropdown device id

        if (deviceListState.isLoading) {
            CircularProgressIndicator()
        }
        else if (deviceListState.data.isNotEmpty()) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedDevice?.device?.device_id ?: "Select a Device",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select a Device") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    deviceListState.data.forEach { device ->
                        DropdownMenuItem(
                            onClick = {
                                selectedDevice = device
                                expanded = false
                            }
                        ) {
                            Text(text = device.device?.device_id ?: "Unknown")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LoadingButton(
            text = "Submit",
            isLoading = isLoading,
            isEnabled = slugId.isNotBlank() && !selectedDevice?.device?.device_id.isNullOrEmpty(), // AJ : Added Enable desaible functionality
            onClick = {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime > 2000) {
                    lastClickTime = currentTime
                    if (slugId.isNotBlank()) {
                        selectedDevice?.let { device ->
                            val deviceId = device.device?.device_id
                            if (deviceId != null) {
                                userInput= employeeViewModel.updateUserInput(deviceId).toString()
                            } else {
                                toaster.show(Toast(message = "Please select device id", type = ToastType.Warning, duration = 2000.milliseconds))
                            }
                        }

                        isLoading = true
                        viewModel.getSchoolDetails(slugId)
                    } else {
                        toaster.show(Toast(message = "Please enter a School Code", type = ToastType.Warning, duration = 2000.milliseconds))
                    }
                }
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        LaunchedEffect(schoolDetails) {
            schoolDetails?.let {
                if (it.isNotEmpty()) {
                    employeeViewModel.getAddressBySchoolId(slugId)
                    toaster.show(Toast(message = "School details retrieved successfully", type = ToastType.Success, duration = 2000.milliseconds))
                    navController.navigate(Screen.SchoolCategoryScreen.createRoute(schoolId = it.first().id))
                } else {
                    toaster.show(Toast(message = "Given Code is invalid", type = ToastType.Error, duration = 2000.milliseconds))
                    isLoading = false
                }
            }
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun Carousel(
    modifier: Modifier = Modifier,
) {
    val images = listOf(
        R.drawable.img_3,
        R.drawable.img_5,
        R.drawable.img_2,
        R.drawable.img_4,
        R.drawable.img_1

    )

    val pagerState = rememberPagerState(
        initialPage = 0
    )
    val scope = rememberCoroutineScope()
    LaunchedEffect(pagerState) {
        while(true) {
            delay(2000)
            scope.launch {
                val nextPage = (pagerState.currentPage + 1) % images.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        HorizontalPager(
            count = images.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = "Carousel Image ${page + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }


        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        ) {
            repeat(images.size) { iteration ->
                val color = if (pagerState.currentPage == iteration)
                    Color.White else Color.Gray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}

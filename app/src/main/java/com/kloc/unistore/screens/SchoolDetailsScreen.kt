package com.kloc.unistore.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.kloc.unistore.model.schoolViewModel.SchoolViewModel
import com.kloc.unistore.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun SchoolDetailsScreen(
    navController: NavHostController,
    viewModel: SchoolViewModel = hiltViewModel()
) {
    var slugId by remember { mutableStateOf("") }
    val toaster = rememberToasterState()
    val schoolDetails by viewModel.schoolDetails.collectAsState(initial = null)
    var isLoading by remember { mutableStateOf(false) }
    var lastClickTime by remember { mutableLongStateOf(0L) }

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

        OutlinedTextField(
            value = slugId,
            onValueChange = { slugId = it },
            label = { Text("Enter Slug ID") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        LoadingButton(
            text = "Search",
            isLoading = isLoading,
            onClick = {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime > 2000) {
                    lastClickTime = currentTime
                    if (slugId.isNotBlank()) {
                        isLoading = true
                        viewModel.getSchoolDetails(slugId)
                    } else {
                        toaster.show(
                            Toast(
                                message = "Please enter a Slug ID",
                                type = ToastType.Warning,
                                duration = 2000.milliseconds
                            )
                        )
                    }
                }
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        LaunchedEffect(schoolDetails) {
            schoolDetails?.let {
                if (it.isNotEmpty()) {
                    toaster.show(
                        Toast(
                            message = "School details retrieved successfully",
                            type = ToastType.Success,
                            duration = 2000.milliseconds
                        )
                    )
                    navController.navigate(Screen.SchoolCategoryScreen.createRoute(schoolId = it.first().id))
                } else {
                    toaster.show(
                        Toast(
                            message = "Given ID is invalid",
                            type = ToastType.Error,
                            duration = 2000.milliseconds
                        )
                    )
                    isLoading = false
                }
            }
        }

        Toaster(
            state = toaster,
            darkTheme = true,
            maxVisibleToasts = 1
        )
        AboutUsSection()
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
@Composable
fun AboutUsSection() {
    val scroll = rememberScrollState()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            //   horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "ABOUT US",
                color = Color(0xFF08090A),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Unistore is your one-stop solution for all educational needs. From uniforms and stationery to shoes and raingears, we cover a wide range of products to cater to your requirements. Our commitment to excellence drives us to continuously expand our product range, ensuring we meet the diverse needs of customers across India.\n\nTo provide a seamless shopping experience, we've partnered with top delivery services like FedEx for safe, contactless delivery. Dedicated customer support ensures that your journey with Unistore is smooth and hassle-free.",
                fontSize = 14.sp,
                color = Color.DarkGray,
                modifier=Modifier.verticalScroll(scroll)
            )
        }
    }
}
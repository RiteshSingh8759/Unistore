package com.kloc.unistore.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kloc.unistore.model.schoolViewModel.SchoolViewModel
import com.kloc.unistore.navigation.Screen

@Composable
fun SchoolDetailsScreen(
    navController: NavHostController,
    viewModel: SchoolViewModel = hiltViewModel()
) {
    var slugId by remember { mutableStateOf("") }
    val context = LocalContext.current
    val schoolDetails by viewModel.schoolDetails.collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TextField for Slug ID
        OutlinedTextField(
            value = slugId,
            onValueChange = { slugId = it },
            label = { Text("Enter Slug ID") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Button to submit slug ID
        Button(
            onClick = {
                viewModel.getSchoolDetails(slugId)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }

        // Observe the schoolDetails state and navigate or show toast based on its value
        LaunchedEffect(schoolDetails) {
            schoolDetails?.let {
                if (it.isNotEmpty()) {
                    navController.navigate(Screen.SchoolCategoryScreen.createRoute(schoolId = it.first().id))
                } else {
                    Toast.makeText(context, "Given ID is invalid", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}

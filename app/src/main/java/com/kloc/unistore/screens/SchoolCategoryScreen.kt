package com.kloc.unistore.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.kloc.unistore.common.CommonProgressIndicator
import com.kloc.unistore.entity.productCategory.Category
import com.kloc.unistore.model.productCategoryViewModel.SchoolCategoryViewModel
import com.kloc.unistore.model.viewModel.MainViewModel
import com.kloc.unistore.navigation.Screen

@Composable
fun SchoolCategoryScreen(
    navController: NavHostController,
    schoolId: Int,
    mainViewModel: MainViewModel,
    viewModel: SchoolCategoryViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsState(initial = emptyList())
    val isCategories by viewModel.isCategories.collectAsState(initial = false)
    LaunchedEffect(Unit) {
        viewModel.fetchCategories(schoolId)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        if (categories.isEmpty()) {
            CommonProgressIndicator(type = "", message ="Loading" )
            if (isCategories) {
                mainViewModel.logOut = true
                navController.navigate(Screen.ProductDetailsScreen.createRoute(schoolId))
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp), // Space between columns
                verticalArrangement = Arrangement.spacedBy(16.dp)   // Space between rows
            ) {
                items(categories) { category ->
                    CategoryItem(category = category) {
                        navController.navigate(Screen.ProductDetailsScreen.createRoute(category.id))
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            AsyncImage(
                model = category.image.src,
                contentDescription = category.image.alt,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomStart)
            )
        }
    }
}
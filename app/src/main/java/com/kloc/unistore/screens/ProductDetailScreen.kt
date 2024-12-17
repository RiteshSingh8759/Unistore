package com.kloc.unistore.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.kloc.unistore.model.productViewModel.ProductViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kloc.unistore.entity.product.Product
import com.kloc.unistore.firestoredb.viewmodel.EmployeeViewModel
import com.kloc.unistore.navigation.Screen

@Composable
fun ProductDetailScreen(
    navController: NavController,
    categoryId: Int,
    viewModel: ProductViewModel
) {
    LaunchedEffect(categoryId) {
        viewModel.getProducts(categoryId)
    }
    val products by viewModel.products.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp) // Uniform horizontal padding
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp), // Consistent spacing between items
            contentPadding = PaddingValues(vertical = 16.dp) // Padding for top and bottom to avoid clipping
        ) {
            items(products) { product ->
                ProductCard(product = product) {
                    navController.navigate(Screen.ProductScreen.createRoute(product.id))
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(

        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp) // Adjusted height for text and image
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Product Image
            val imageUrl = product.images.firstOrNull()?.src ?: ""
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Ensures image takes equal space
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(1.5f),
                    contentScale = ContentScale.Crop
                )
            }

            // Spacer for separation
            Spacer(modifier = Modifier.height(8.dp))

            // Product Name
            Text(
                text = product.name,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp) // Padding around text
            )
        }
    }
}
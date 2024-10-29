package com.kloc.unistore.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kloc.unistore.model.productViewModel.ProductViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kloc.unistore.entity.product.Product

@Composable
fun ProductDetailScreen(
    navController: NavController,
    categoryId: Int,
    viewModel: ProductViewModel = hiltViewModel()
) {
    // Call the ViewModel to fetch products when the screen is displayed
    LaunchedEffect(categoryId) {
        viewModel.getProducts(categoryId)
    }

    val products by viewModel.products.collectAsState() // Collecting state

    // UI to display products
    LazyColumn {
        items(products) { product ->
            ProductCard(product)
        }
    }
}

@Composable
fun ProductCard(product: Product) {
    var quantity by remember { mutableStateOf(0) }
    var selectedSize by remember { mutableStateOf(product.attributes.firstOrNull()?.options?.firstOrNull()) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Product Image
            val imageUrl = product.images.firstOrNull()?.src ?: ""
            AsyncImage(
                model = imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2, // Allows for wrapping if the name is long
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (product.stock_status == "instock") "In Stock" else "Out of Stock",
                    color = if (product.stock_status == "instock") Color.Black else Color.Red,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "MRP ₹${product.price}",
                    color = Color.Gray,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Size Dropdown (if applicable)
                if (product.attributes.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Size:", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))

                        Box {
                            Text(
                                text = selectedSize ?: "Select",
                                modifier = Modifier
                                    .clickable { isDropdownExpanded = true }
                                    .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                                    .padding(8.dp),
                                color = Color.DarkGray
                            )

                            DropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false }
                            ) {
                                product.attributes.forEach { attribute ->
                                    attribute.options.forEach { size ->
                                        DropdownMenuItem(onClick = {
                                            selectedSize = size
                                            isDropdownExpanded = false
                                        }) {
                                            Text(text = size)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quantity Control with Icons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = { if (quantity > 0) quantity-- },
                        modifier = Modifier.size(32.dp),
                        colors = ButtonDefaults.textButtonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("-", fontSize = 18.sp, color = Color.Black) // Set text color explicitly
                    }

                    Text(
                        text = quantity.toString(),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontSize = 16.sp
                    )

                    TextButton(
                        onClick = { quantity++ },
                        modifier = Modifier.size(32.dp),
                        colors = ButtonDefaults.textButtonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("+", fontSize = 18.sp, color = Color.Black) // Set text color explicitly
                    }
                }


                Spacer(modifier = Modifier.height(8.dp))

                // Add to Cart Button
                Button(
                    onClick = { /* Logic to add product to cart with selectedSize and quantity */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                ) {
                    Text("Add to Cart", color = Color.White)
                }
            }
        }
    }
}

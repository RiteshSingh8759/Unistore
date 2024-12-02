package com.kloc.unistore.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.kloc.unistore.entity.cart.CartItem
import com.kloc.unistore.model.viewModel.MainViewModel
import com.kloc.unistore.navigation.Screen

@Composable
fun CartScreen(navController: NavHostController, mainViewModel: MainViewModel) {
    val cartItems by mainViewModel.cartViewModel.cartItems.collectAsState()
    val scrollState = rememberScrollState() // Add scroll state for vertical scrolling

    Box(modifier = Modifier.fillMaxSize()) {
        if (cartItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Your cart is empty", fontSize = 18.sp, color = Color.Gray)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState) // Enable vertical scrolling
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f), // Allows LazyColumn to occupy available space
                    contentPadding = PaddingValues(bottom = 72.dp) // Add padding for button spacing
                ) {
                    items(cartItems) { product ->
                        CartProductCard(product, mainViewModel)
                    }
                }

                Button(
                    onClick = {
                        navController.navigate(Screen.OrderDetailsScreen.route) // Adjust navigation as needed
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp), // Set height for button
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                ) {
                    Text("Checkout", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun CartProductCard(cartProduct: CartItem, mainViewModel: MainViewModel) {
    var quantity by remember { mutableStateOf(cartProduct.quantity) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = cartProduct.product.images.firstOrNull()?.src ?: "",
                    contentDescription = cartProduct.product.name,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = cartProduct.product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 2)
                    Text(text = "Size: "+cartProduct.size, fontSize = 14.sp, maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = if (cartProduct.product.stock_status == "instock") "In Stock" else "Out of Stock",
                    color = if (cartProduct.product.stock_status == "instock") Color.Black else Color.Red,
                    fontSize = 14.sp)
                Text(text = "MRP ₹${cartProduct.product.price}", color = Color.Gray, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quantity Control with Icons
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Absolute.SpaceEvenly) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = {
                        if (quantity > 1) {
                            quantity--
                            mainViewModel.cartViewModel.updateQuantity(cartProduct, quantity)
                        }
                    }) {
                        Text("-", fontSize = 18.sp, color = Color.Black)
                    }

                    Text(text = quantity.toString(), modifier = Modifier.padding(horizontal = 16.dp), fontSize = 16.sp)

                    TextButton(onClick = {
                        quantity++
                        mainViewModel.cartViewModel.updateQuantity(cartProduct, quantity)
                    }) {
                        Text("+", fontSize = 18.sp, color = Color.Black)
                    }
                }
                Button(onClick = { mainViewModel.cartViewModel.removeFromCart(cartProduct) }) {
                    Text("Remove from Cart", color = Color.White)
                }
            }
        }
    }
}

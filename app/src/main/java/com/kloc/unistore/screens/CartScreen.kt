package com.kloc.unistore.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.kloc.unistore.R
import com.kloc.unistore.entity.cart.CartItem
import com.kloc.unistore.model.viewModel.MainViewModel
import com.kloc.unistore.navigation.Screen

@Composable
fun CartScreen(navController: NavHostController, mainViewModel: MainViewModel) {
    val cartItems by mainViewModel.cartViewModel.cartItems.collectAsState()
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize().background(Color.Transparent)) {
        if (cartItems.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img),
                    contentDescription = "Empty cart",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(8.dp) // Reduced padding
                )
            }

        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp) // Reduced horizontal padding
                    .background(Color.Transparent)
                    .verticalScroll(scrollState)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 36.dp, top = 8.dp) // Reduced padding
                ) {
                    items(cartItems) { product ->
                        CartProductCard(product, mainViewModel)
                    }
                }

                Button(
                    onClick = {
                        navController.navigate(Screen.StudentDetailsScreen.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp) // Reduced padding
                        .height(48.dp), // Reduced height
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                ) {
                    Text("Checkout", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CartProductCard(cartProduct: CartItem, mainViewModel: MainViewModel) {
    var quantity by remember { mutableStateOf(cartProduct.quantity) }
    var min_Quantity by remember { mutableStateOf(cartProduct.min_Quantity) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp), // Reduced padding
        shape = RoundedCornerShape(8.dp), // Reduced corner radius
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Reduced elevation
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(8.dp)) { // Reduced padding
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = cartProduct.product.images.firstOrNull()?.src ?: "",
                    contentDescription = cartProduct.product.name,
                    modifier = Modifier
                        .size(80.dp) // Reduced image size
                        .clip(RoundedCornerShape(6.dp)) // Reduced corner radius
                        .background(Color(0xFFFFFEFE))
                )
                Spacer(modifier = Modifier.width(8.dp)) // Reduced spacer width
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = cartProduct.product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp, // Reduced font size
                        maxLines = 2,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(2.dp)) // Reduced spacer height
                    Text(text = "Size: " + cartProduct.size, fontSize = 15.sp, color = Color.DarkGray) // Reduced font size
                }
            }

            Spacer(modifier = Modifier.height(4.dp)) // Reduced spacer height

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (cartProduct.product.stock_status == "instock") "In Stock" else "Out of Stock",
                    color = if (cartProduct.product.stock_status == "instock") Color.Green else Color.Red,
                    fontSize = 12.sp, // Reduced font size
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "MRP ₹${cartProduct.product.price}",
                    color = Color.DarkGray,
                    fontSize = 14.sp, // Reduced font size
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(4.dp)) // Reduced spacer height

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = {
                            if (quantity > min_Quantity) {
                                quantity--
                                mainViewModel.cartViewModel.updateQuantity(cartProduct, quantity)
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                    ) {
                        Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold) // Adjusted font size
                    }

                    Text(
                        text = quantity.toString(),
                        fontSize = 14.sp, // Reduced font size
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 4.dp) // Reduced padding
                    )

                    TextButton(
                        onClick = {
                            quantity++
                            mainViewModel.cartViewModel.updateQuantity(cartProduct, quantity)
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                    ) {
                        Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold) // Adjusted font size
                    }
                }

                Button(
                    onClick = { mainViewModel.cartViewModel.removeFromCart(cartProduct) },
                    modifier = Modifier.height(32.dp), // Reduced height
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                ) {
                    Text("Remove", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold) // Reduced font size
                }
            }
        }
    }
}
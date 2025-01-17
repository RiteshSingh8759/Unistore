package com.kloc.unistore.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.*
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.*
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
            val lottieComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.emptycart))
            val lottieProgress by animateLottieCompositionAsState(lottieComposition, iterations = LottieConstants.IterateForever)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LottieAnimation(composition = lottieComposition, progress = lottieProgress, modifier = Modifier.fillMaxWidth())
            }

        } else {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp).background(Color.Transparent).verticalScroll(scrollState)) {
                LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(bottom = 36.dp, top = 8.dp) ) {
                    items(cartItems) { product ->
                        CartProductCard(product, mainViewModel)
                    }
                }

                Button(
                    onClick = { navController.navigate(Screen.StudentDetailsScreen.route) },
                    modifier = Modifier.fillMaxWidth().padding(8.dp).height(48.dp),
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
    Card(modifier = Modifier.fillMaxWidth().padding(4.dp), shape = RoundedCornerShape(8.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = cartProduct.product.images.firstOrNull()?.src ?: R.drawable.image,
                    contentDescription = cartProduct.product.name,
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(6.dp)).background(Color(0xFFFFFEFE))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = cartProduct.product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 2, color = Color.Black)
                    if (cartProduct.size.trim().isNotEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Size: ",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            LazyColumn(modifier = Modifier
                                .heightIn(max = 100.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)) {
                                item {
                                    Text(
                                        text = cartProduct.size,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
//                        Text(text = "Size: " + cartProduct.size, fontSize = 15.sp, color = Color.DarkGray)
                    }
                    if(cartProduct.color.trim().isNotEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Color: " + cartProduct.color,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (cartProduct.product.stock_status == "instock") "In Stock" else "Out of Stock",
                    color = if (cartProduct.product.stock_status == "instock") Color(0xFF388E3C) else Color(0xFFB71C1C),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(text = "MRP ₹${cartProduct.product.price}", color = Color.DarkGray, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
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
                        Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(text = quantity.toString(), fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 4.dp))
                    TextButton(
                        onClick = {
                            quantity++
                            mainViewModel.cartViewModel.updateQuantity(cartProduct, quantity)
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                    ) {
                        Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Button(onClick = { mainViewModel.cartViewModel.removeFromCart(cartProduct) }, modifier = Modifier.height(32.dp), colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)) {
                    Text("Remove", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}



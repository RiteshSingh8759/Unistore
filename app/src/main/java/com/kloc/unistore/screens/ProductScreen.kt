package com.kloc.unistore.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.kloc.unistore.entity.product.Product
import com.kloc.unistore.model.productViewModel.ProductViewModel
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.layout.ContentScale
import com.kloc.unistore.model.cartViewModel.CartViewModel
import com.kloc.unistore.model.orderViewModel.OrderViewModel
import com.kloc.unistore.model.viewModel.MainViewModel

@Composable
fun ProductScreen(
    navController: NavController,
    productId: Int,
    viewModel: ProductViewModel,
    mainViewModel: MainViewModel
) {
    // State to collect the list of bundled products
    val bundledProducts by viewModel.bundledProducts.collectAsState(initial = emptyList())
    val productDetails by viewModel.productDetails.collectAsState()
    var productItemMap: HashMap<Int, Int> = hashMapOf()


    // Fetch product details using the productId
    LaunchedEffect(productId) {
        viewModel.fetchProductDetailsById(productId) // Fetch product details by ID

    }

    // Use LaunchedEffect to fetch bundled products when product details are available
    LaunchedEffect(productDetails) {
        productDetails?.let { product ->
            val bundledProductIds = product.bundled_items.map { it.product_id }
            // Iterate over the bundled_items list and populate the HashMap
            product.bundled_items.forEach { item ->
                val productId = item.product_id // Replace with the actual way to fetch productId from 'item'
                val itemId = item.bundled_item_id       // Replace with the actual way to fetch itemId from 'item'

                // If the productId is already in the map, append the new itemId to the existing list
                productItemMap[productId] = itemId
            }
            viewModel.fetchBundledProducts(bundledProductIds) // Fetch bundled products
        }
    }

    // Display product details
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Check if product details are loaded
        if (productDetails != null) {
            Text(
                text = "Product Name: ${productDetails!!.name}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

        } else {
            // Optionally show a loading state or a placeholder
            Text(text = "Loading product details...", fontSize = 20.sp)
        }

        // Display bundled products
        LazyColumn {
            items(bundledProducts) { bundledProduct ->
                ProductCard(product = bundledProduct, mainViewModel,productItemMap) // Show each fetched bundled product
            }
        }
    }
}


@Composable
fun ProductCard(product: Product, mainViewModel: MainViewModel,productItemMap:HashMap<Int,Int>) {
    var quantity by remember { mutableStateOf(0) }
    var selectedSize by remember { mutableStateOf(product.attributes.firstOrNull()?.options?.firstOrNull()) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isCustomSizeChecked by remember { mutableStateOf(false) } // Track checkbox state
    var sizeType by remember { mutableStateOf("")}
    var variationId by remember { mutableStateOf(0)}
    var itemId by remember { mutableStateOf(0)}
    var index by remember { mutableStateOf(0)}

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
                val imageUrl = product.images.firstOrNull()?.src ?: ""
                AsyncImage(
                    model = imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop, // Ensures the image fills the entire space
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 2,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (product.stock_status == "instock") "In Stock" else "Out of Stock",
                    color = if (product.stock_status == "instock") Color.Black else Color.Red,
                    fontSize = 14.sp
                )
                Text(text = "MRP ₹${product.price}", color = Color.Gray, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp) ) {

                if (!isCustomSizeChecked) {
                    Text(text = "Size:", fontSize = 14.sp)

                    // Dropdown for size selection
                    Box {
                        Text(
                            text = selectedSize ?: "Select",
                            modifier = Modifier
                                .clickable { isDropdownExpanded = true }
                                .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                                .padding(8.dp),
                            color = Color.DarkGray
                        )

                        DropdownMenu(expanded = isDropdownExpanded, onDismissRequest = { isDropdownExpanded = false }) {
                            product.attributes.forEach { attribute ->
                                attribute.options.forEach { size ->
                                    DropdownMenuItem(onClick = {
                                        selectedSize = size
                                        index=attribute.options.indexOf(selectedSize)
                                        isDropdownExpanded = false
                                    }) {
                                        Text(text = size)
                                    }
                                }
                            }
                        }
                    }
                }


                Checkbox(checked = isCustomSizeChecked, onCheckedChange = { isCustomSizeChecked = it })

                // Display custom size text if checkbox is unchecked
                if (!isCustomSizeChecked) { Text(text = "Custom", fontSize = 14.sp) }
                if (isCustomSizeChecked) { Text(text = " Size", fontSize = 14.sp)}

                // Show custom size TextField if checkbox is checked
                if (isCustomSizeChecked) {
                    TextField(
                        value = selectedSize?:"",
                        onValueChange = { selectedSize = it },
                        label = { Text("Enter Custom Size") },
                        modifier = Modifier
                            .height(56.dp)
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quantity Control with Icons
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Absolute.SpaceEvenly) {
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
                Button(
                    onClick = {
                        if(isCustomSizeChecked)
                        {
                            sizeType="Custom"
                            variationId=0
                        }
                        else{
                            sizeType="Size"
                            variationId = product.variations.getOrElse(index) { 0 } .toString().toDouble().toInt()
                        }
                        itemId= productItemMap[product.id]!!
                        mainViewModel.showToast(
                            mainViewModel.cartViewModel.addToCart(
                                product,
                                quantity,
                                selectedSize,
                                sizeType,
                                variationId,
                                itemId
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                    enabled = quantity > 0
                ) {
                    Text("Add to Cart", color = Color.White)
                }
            }
        }
    }
}

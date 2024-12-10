package com.kloc.unistore.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.kloc.unistore.entity.product.Product
import com.kloc.unistore.model.productViewModel.ProductViewModel
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.layout.ContentScale
import com.kloc.unistore.common.CommonProgressIndicator
import com.kloc.unistore.model.viewModel.MainViewModel

@Composable
fun ProductScreen(
    navController: NavController,
    productId: Int,
    viewModel: ProductViewModel,
    mainViewModel: MainViewModel
) {
    val bundledProducts by viewModel.bundledProducts.collectAsState(initial = emptyList())
    val productDetails by viewModel.productDetails.collectAsState()
    val productItemMap = remember { mutableStateMapOf<Int, MutableList<Pair<Int, Int>>>() }
    var isLoading by remember{mutableStateOf(false)}
    LaunchedEffect(productId) {
        isLoading = true
        viewModel.fetchProductDetailsById(productId)
    }
    LaunchedEffect(productDetails, bundledProducts) {
        isLoading = productDetails == null || bundledProducts.isEmpty()
    }
    LaunchedEffect(productDetails) {
        productDetails?.let { product ->
            val bundledProductIds = product.bundled_items.map { it.product_id }
            product.bundled_items.forEach { item ->
                val valueList = productItemMap.getOrPut(item.product_id) { mutableListOf() }

                // Add the pair (bundled_item_id, quantity_min) to the list
                valueList.add(item.bundled_item_id to item.quantity_min)
            }
            viewModel.fetchBundledProducts(bundledProductIds)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            isLoading -> CommonProgressIndicator(":package:", "Loading product details...")
            productDetails != null -> {
                Text(text = "Product Name: ${productDetails!!.name}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) { items(bundledProducts) { bundledProduct -> ProductCard(product = bundledProduct, mainViewModel = mainViewModel, productItemMap = productItemMap) } }
            }
            else -> Text("Product not found.", color = Color.Red)
        }

    }
}

@Composable
fun ProductCard(product: Product, mainViewModel: MainViewModel, productItemMap: SnapshotStateMap<Int, MutableList<Pair<Int, Int>>>) {
    val initialItemPair = productItemMap[product.id]?.firstOrNull() ?: (0 to 0)
    var quantity by remember {  mutableStateOf(initialItemPair.second) }
    var selectedSize by remember { mutableStateOf(product.attributes.firstOrNull()?.options?.firstOrNull()) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isCustomSizeChecked by remember { mutableStateOf(false) }
    var sizeType by remember { mutableStateOf("") }
    var variationId by remember { mutableStateOf(0) }
    var index by remember { mutableStateOf(0) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val imageUrl = product.images.firstOrNull()?.src ?: ""
                AsyncImage(
                    model = imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
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
                        fontSize = 18.sp,
                        maxLines = 2,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (product.stock_status == "instock") "In Stock" else "Out of Stock",
                color = if (product.stock_status == "instock") Color(0xFF388E3C) else Color.Red,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "MRP ₹${product.price}",
                color = Color.DarkGray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (product.attributes.any { it.options.isNotEmpty() }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (!isCustomSizeChecked) {
                    Text(
                        text = "Size:",
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Box(
                        modifier = Modifier
                            .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                            .clickable { isDropdownExpanded = true }
                            .padding(8.dp)
                    ) {
                        Text(
                            text = selectedSize ?: "Select",
                            fontSize = 14.sp,
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
                                        index = attribute.options.indexOf(size)
                                        isDropdownExpanded = false
                                    }) {
                                        Text(text = size)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))
                Checkbox(
                    checked = isCustomSizeChecked,
                    onCheckedChange = { isCustomSizeChecked = it },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(
                    text = "Custom Size",
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }

            if (isCustomSizeChecked) {
                TextField(
                    value = selectedSize ?: "",
                    onValueChange = { selectedSize = it },
                    label = { Text("Enter Custom Size") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { if (quantity > initialItemPair.second) quantity-- }) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                }
                Text(text = quantity.toString(), fontSize = 16.sp)
                IconButton(onClick = { quantity++ }) {
                    Icon(Icons.Default.Add, contentDescription = "Increase")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    sizeType = if (isCustomSizeChecked) "Custom" else "Size"
                    variationId = product.variations.getOrElse(index) { 0 }.toString().toDouble().toInt()
                    val itemId = initialItemPair.first
                    // Default to "No Size" for products without size options
                    val sizeToAdd = if (product.attributes.isEmpty()) "No Size" else selectedSize
                    mainViewModel.showToast(
                        mainViewModel.cartViewModel.addToCart(
                            product = product,
                            quantity = quantity,
                            min_Quantity=initialItemPair.second,
                            selectedSize = sizeToAdd,
                            sizeType = sizeType,
                            variationId = variationId,
                            itemId = itemId
                        )
                    )
                    quantity = initialItemPair.second
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = quantity > 0
            ) {
                Text("Add to Cart", fontSize = 16.sp)
            }
        }
    }
}

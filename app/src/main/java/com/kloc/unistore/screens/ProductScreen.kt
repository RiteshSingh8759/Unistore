package com.kloc.unistore.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.input.key.Key.Companion.Copy
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.room.util.copy
import com.dokar.sonner.Toast
import com.dokar.sonner.ToastType
import com.dokar.sonner.Toaster
import com.dokar.sonner.rememberToasterState
import com.kloc.unistore.common.CommonProgressIndicator
import com.kloc.unistore.model.viewModel.MainViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

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
            isLoading -> CommonProgressIndicator("Loading Products")
            productDetails != null -> {
                mainViewModel.className = productDetails!!.name
                Text(
                    text = productDetails!!.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                LazyColumn(modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)) {
                    items(bundledProducts) { bundledProduct ->
                        ProductCard(
                            product = bundledProduct,
                            mainViewModel = mainViewModel,
                            productItemMap = productItemMap
                        )
                    }
                }
            }
            else -> Text("Product not found.", color = Color.Red)
        }
    }
}


@Composable
fun ProductCard(product: Product, mainViewModel: MainViewModel, productItemMap: SnapshotStateMap<Int, MutableList<Pair<Int, Int>>>) {

    val toaster = rememberToasterState()
    Toaster(state = toaster, darkTheme = true, maxVisibleToasts = 1)

    var showBottomSheet by remember { mutableStateOf(false) }
    var customSizeSelectedOption by remember { mutableStateOf("") }
    var isCustomSizeChecked by remember { mutableStateOf(false) }


    val initialItemPair = productItemMap[product.id]?.firstOrNull() ?: (0 to 0)
    var quantity by remember { mutableStateOf(initialItemPair.second) }
    var selectedSize by remember { mutableStateOf(product.attributes.find { it.name == "Size" }?.options?.firstOrNull()) }
    var selectedColor by remember { mutableStateOf(product.attributes.find { it.name == "Color" }?.options?.firstOrNull()) }
    var isSizeDropdownExpanded by remember { mutableStateOf(false) }
    var isColorDropdownExpanded by remember { mutableStateOf(false) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var sizeType by remember { mutableStateOf("") }
    var variationId by remember { mutableStateOf(0) }
    var index by remember { mutableStateOf(0) }
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp), shape = RoundedCornerShape(8.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant) // Updated background color
            .padding(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                val imageUrl = product.images.firstOrNull()?.src ?: ""
                AsyncImage(
                    model = imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (product.stock_status == "instock") "In Stock" else "Out of Stock",
                color = if (product.stock_status == "instock")  MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error, // Updated color for stock status
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "MRP ₹${product.price}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (product.attributes.any { it.options.isNotEmpty() }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    val availableSizes = product.attributes.find { it.name == "Size" }?.options.orEmpty()
                    val availableColors = product.attributes.find { it.name == "Color" }?.options.orEmpty()
                    if (availableSizes.isNotEmpty() && !isCustomSizeChecked) {
                        AttributeDropdown(
                            label = "Size",
                            options = availableSizes,
                            selectedOption = selectedSize,
                            onOptionSelected = { selectedSize = it },
                            isExpanded = isSizeDropdownExpanded,
                            onExpandChanged = { isSizeDropdownExpanded = it }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    if (availableColors.isNotEmpty()) {
                        AttributeDropdown(
                            label = "Color",
                            options = availableColors,
                            selectedOption = selectedColor,
                            onOptionSelected = { selectedColor = it },
                            isExpanded = isColorDropdownExpanded,
                            onExpandChanged = { isColorDropdownExpanded = it }
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))
                    // Checkbox row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Checkbox(
                            checked = isCustomSizeChecked,
                            onCheckedChange = {
                                isCustomSizeChecked = it
                                if (!it) {
                                    selectedSize = product.attributes.find { it.name == "Size" }?.options?.firstOrNull()
                                    customSizeSelectedOption = ""
                                    showBottomSheet = false
                                } else {
                                    selectedSize = ""
                                }
                            },
                            modifier = Modifier.size(24.dp),
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF388E3C), uncheckedColor = Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Custom Size", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground) // Updated text color
                    }
                    // Radio buttons
                    if (isCustomSizeChecked) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = customSizeSelectedOption == "Top",
                                onClick = {
                                    customSizeSelectedOption = "Top"
                                    showBottomSheet = true
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF388E3C))
                            )
                            Text(
                                text = "Top",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground // Updated text color
                            )

                            RadioButton(
                                selected = customSizeSelectedOption == "Bottom",
                                onClick = {
                                    customSizeSelectedOption = "Bottom"
                                    showBottomSheet = true
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF388E3C))
                            )
                            Text(
                                text = "Bottom",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground // Updated text color
                            )
                        }
                        // Bottom Sheet
                        if (showBottomSheet) {
                            CustomSizeChart(
                                selectedOption = customSizeSelectedOption,
                                onDismiss = { showBottomSheet = false },
                                sizeChartCallback = { size ->
                                    selectedSize = size
                                    showBottomSheet = false
                                }
                            )
                        }

                    } else {
                        customSizeSelectedOption = ""
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }
                if (isCustomSizeChecked && selectedSize?.isNotEmpty() == true) {
                    Text(text = "Size: $selectedSize", fontSize = 14.sp, modifier = Modifier.padding(end = 16.dp), color = MaterialTheme.colorScheme.onSurface) // Updated text color
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { if (quantity > initialItemPair.second) quantity-- },
                    modifier = Modifier
                        .padding(4.dp)
                        .size(24.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = MaterialTheme.colorScheme.primary) // Updated color
                }
                Text(text = quantity.toString(), fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface) // Updated text color
                IconButton(
                    onClick = { quantity++ },
                    modifier = Modifier
                        .padding(4.dp)
                        .size(24.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = MaterialTheme.colorScheme.primary) // Updated color
                }
            }
            Button(
                onClick = {
                    sizeType = if (isCustomSizeChecked) "Custom" else "Size"
                    variationId =
                        product.variations.getOrElse(index) { 0 }.toString().toDouble().toInt()
                    val itemId = initialItemPair.first
                    // Default to "No Size" for products without size options
                    val sizeToAdd = if (product.attributes.find { it.name == "Size" }?.options?.isEmpty() == true) "" else selectedSize
                    val response = mainViewModel.cartViewModel.addToCart(
                        product = product,
                        quantity = quantity,
                        min_Quantity = initialItemPair.second,
                        selectedSize = sizeToAdd,
                        selectedColor = selectedColor ?: "",
                        sizeType = sizeType,
                        variationId = variationId,
                        itemId = itemId
                    ) // AJ : Removed Toast
                    when (response) {
                        "Product with selected size and color already exists.Quantity updated by $quantity" -> {
                            toaster.show(Toast(message = response, type = ToastType.Info, duration = 2000.milliseconds))
                        } // AJ : Toast Added
                        "Product added to cart." -> {
                            toaster.show(Toast(message = response, type = ToastType.Success, duration = 2000.milliseconds))
                        } // AJ : Added Toast
                        "Please select the size." -> {
                            toaster.show(Toast(message = response, type = ToastType.Success, duration = 2000.milliseconds))
                        } // AJ : Added Toast
                    }
                    selectedSize = product.attributes.find { it.name == "Size" }?.options?.firstOrNull()
                    isCustomSizeChecked = false
                    quantity = initialItemPair.second
                },
                modifier = Modifier.height(32.dp),
                enabled = quantity > 0 && (!isCustomSizeChecked || (isCustomSizeChecked && !selectedSize.isNullOrEmpty())),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) // Updated button color
            ) {
                Text("Add to Cart", fontSize = 12.sp) // Updated text color
            }
        }
    }
}

@Composable
fun AttributeDropdown(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    isExpanded: Boolean,
    onExpandChanged: (Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "$label:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground) // Adjust label color based on background
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(4.dp))
                .clickable { onExpandChanged(true) }
                .padding(6.dp)
        ) {
            Text(
                text = selectedOption ?: "Select",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground // Adjust selected option text color based on background
            )
            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { onExpandChanged(false) }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(onClick = {
                        onOptionSelected(option)
                        onExpandChanged(false)
                    }) {
                        Text(text = option, color = MaterialTheme.colorScheme.onBackground) // Adjust option text color
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSizeChart(
    selectedOption: String,
    onDismiss: () -> Unit,
    sizeChartCallback: (String) -> Unit // Add the callback
) {
    Log.d("debug", "Bottom sheet should show now")

    // The resulting size string to be passed back when "Done" is clicked
    var sizeString by remember { mutableStateOf("") }
    val textColor = MaterialTheme.colorScheme.onBackground

    ModalBottomSheet(onDismissRequest = onDismiss, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "$selectedOption Custom Size", fontWeight = FontWeight.Bold, color = textColor)
            Spacer(modifier = Modifier.height(16.dp))

            // Depending on the selected option, show the respective input fields
            when (selectedOption) {
                "Top" -> { sizeString = ShirtInputFields() }
                "Bottom" -> { sizeString = TrouserInputFields() }
                else -> Text("Select an option to enter custom sizes", color = textColor)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // "Done" or "Save" button to confirm the input and pass the size string
            Button(
                onClick = {
                    sizeChartCallback(sizeString) // Pass the size string to callback
                    onDismiss() // Dismiss the modal after clicking Done
                },
                enabled = sizeString.isNotEmpty(),
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(text = "Done", color = textColor)
            }
        }
    }
}



@Composable
fun ShirtInputFields(): String {
    val fields = listOf(
        "Chest (C)" to "C",   // Show full label but use abbreviation for return
        "Length (L)" to "L",
        "Sleeves (SL)" to "SL",
        "Shoulder (S)" to "S",
        "Waist (W)" to "W",
        "Hip (H)" to "H"
    )

    val fieldValues = remember { mutableStateOf(fields.associate { it.second to "" }) }
    val errorMessages = remember { mutableStateOf(mapOf<String, String>()) }

    val backgroundColor = MaterialTheme.colorScheme.onBackground // You can change this to any color
    val textColor = MaterialTheme.colorScheme.onBackground // This will automatically adjust based on the background color

    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        items(fields) { (label, field) ->
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        color = textColor // Dynamic text color based on background color
                    )
                    TextField(
                        value = fieldValues.value[field] ?: "",
                        onValueChange = { newValue ->
                            val regex = "^(\\d+(\\.\\d{0,2})?)?$".toRegex()  // regex to allow only up to two decimal places
                            val newValueDouble = newValue.toDoubleOrNull()

                            if (newValue.isEmpty()) {
                                fieldValues.value = fieldValues.value.toMutableMap().apply { this[field] = "" }
                                errorMessages.value = errorMessages.value - field
                            } else if (newValueDouble != null && newValueDouble in 1.0..200.0 && newValue.matches(regex)) {
                                fieldValues.value = fieldValues.value.toMutableMap().apply { this[field] = newValue }
                                errorMessages.value = errorMessages.value - field
                            } else {
                                errorMessages.value = errorMessages.value + (field to "Valid size must be between 1 and 200")
                            }
                        },
                        modifier = Modifier
                            .width(70.dp)
                            .height(45.dp)
                            .background(MaterialTheme.colorScheme.outline) // Set background color for input field
                            .horizontalScroll(rememberScrollState()),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        maxLines = 1,
                        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground) // Set dynamic text color in the input field
                    )
                }
                errorMessages.value[field]?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    return buildString {
        fieldValues.value.forEach { (field, value) ->
            if (value.isNotEmpty()) {
                append("$field-$value, ")
            }
        }
        if (this.isNotEmpty()) setLength(this.length - 2) // Remove trailing comma and space
    }
}

@Composable
fun TrouserInputFields(): String {
    val fields = listOf(
        "Waist (W)" to "W",   // Show full label but use abbreviation for return
        "Length (L)" to "L",
        "Thigh (Th)" to "Th",
        "U" to "U",
        "Trouser Bottom (TB)" to "TB"
    )

    val fieldValues = remember { mutableStateOf(fields.associate { it.second to "" }) }
    val errorMessages = remember { mutableStateOf(mapOf<String, String>()) }

    val backgroundColor = MaterialTheme.colorScheme.primary // You can change this to any color
    val textColor = MaterialTheme.colorScheme.onBackground // This will automatically adjust based on the background color

    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        items(fields) { (label, field) ->
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        color = textColor // Dynamic text color based on background color
                    )
                    TextField(
                        value = fieldValues.value[field] ?: "",
                        onValueChange = { newValue ->
                            val regex = "^(\\d+(\\.\\d{0,2})?)?$".toRegex()  // regex to allow only up to two decimal places
                            val newValueDouble = newValue.toDoubleOrNull()

                            if (newValue.isEmpty()) {
                                fieldValues.value = fieldValues.value.toMutableMap().apply { this[field] = "" }
                                errorMessages.value = errorMessages.value - field
                            } else if (newValueDouble != null && newValueDouble in 1.0..200.0 && newValue.matches(regex)) {
                                fieldValues.value = fieldValues.value.toMutableMap().apply { this[field] = newValue }
                                errorMessages.value = errorMessages.value - field
                            } else {
                                errorMessages.value = errorMessages.value + (field to "Valid size must be between 1 and 200")
                            }
                        },
                        modifier = Modifier
                            .width(70.dp)
                            .height(45.dp)
                            .background(backgroundColor) // Set background color for input field
                            .horizontalScroll(rememberScrollState()),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        maxLines = 1,
                        textStyle = LocalTextStyle.current.copy(color = textColor) // Set dynamic text color in the input field
                    )
                }
                errorMessages.value[field]?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    return buildString {
        fieldValues.value.forEach { (field, value) ->
            if (value.isNotEmpty()) {
                append("$field-$value, ")
            }
        }
        if (this.isNotEmpty()) setLength(this.length - 2) // Remove trailing comma and space
    }
}

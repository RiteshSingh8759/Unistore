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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kloc.unistore.entity.cart.CartItem
import com.kloc.unistore.entity.order.Billing
import com.kloc.unistore.entity.order.LineItem
import com.kloc.unistore.entity.order.Order
import com.kloc.unistore.entity.order.OrderMetaData
import com.kloc.unistore.entity.order.Shipping
import com.kloc.unistore.entity.order.ShippingLine
import com.kloc.unistore.entity.product.MetaData
import com.kloc.unistore.model.orderViewModel.OrderViewModel
import com.kloc.unistore.model.schoolViewModel.SchoolViewModel
import com.kloc.unistore.model.viewModel.MainViewModel

@Composable
fun OrderDetailsScreen(navController: NavController, mainViewModel: MainViewModel) {
    val orderViewModel: OrderViewModel = hiltViewModel()
    val schoolViewModel: SchoolViewModel = hiltViewModel()
    val schoolDetails by schoolViewModel.schoolDetails.collectAsState()
    val cartItems by mainViewModel.cartViewModel.cartItems.collectAsState()
    val totalAmount = cartItems.sumOf { it.product.price * it.quantity }

    val order = Order(
        payment_method = "bacs",
        payment_method_title = "Direct Bank Transfer",
        set_paid = true,
        billing = Billing(
            first_name = "John",
            last_name = "Doe",
            address_1 = "969 Market",
            address_2 = "",
            city = "San Francisco",
            state = "CA",
            postcode = "94103",
            country = "US",
            email = "john.doe@example.com",
            phone = "(555) 555-5555"
        ),
        shipping = Shipping(
            first_name = "John",
            last_name = "Doe",
            address_1 = "969 Market",
            address_2 = "",
            city = "San Francisco",
            state = "CA",
            postcode = "94103",
            country = "US"
        ),
        line_items = cartItems.map {
            LineItem(
                product_id = it.product.id,  // Assuming the product has an `id`
                quantity = it.quantity,
                meta_data = listOf(
                    OrderMetaData(key = "tmcp_textfield_0", value = "Rishab Thejakumar Kaleyanda"),
                    OrderMetaData(key = "tmcp_textfield_1", value = "Rashmi B Kumbugowdana"),
                    OrderMetaData(key = "tmcp_textfield_2", value = "XI A"),
                    OrderMetaData(key = "tmcp_textfield_3", value = schoolDetails?.firstOrNull()?.id?.toString() ?: "Unknown"),
                    OrderMetaData(key = "tmcp_textfield_4", value = "M_0"),
                    OrderMetaData(key = "custom_size", value = it.size)
                )
            )
        },
        shipping_lines = listOf(
            ShippingLine(
                method_id = "flat_rate",
                method_title = "Flat Rate",
                total = "10.00"
            )
        )
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Order Details",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Product Details
        Text(
            text = "Products:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.Start)) {
            // Use LazyColumn to make the list scrollable
            LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                items(cartItems) { cartItem ->
                    ProductDetailRow(cartItem)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Shipping Address
        Text(
            text = "Shipping Address:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "123 Street Name, City, State, ZIP",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Total Amount
        Text(
            text = "Total Amount: ₹$totalAmount",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Payment Type and Status Dropdowns
        Text(
            text = "Payment Details:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            var selectedPaymentType by remember { mutableStateOf("") }
            var selectedPaymentStatus by remember { mutableStateOf("") }
            val paymentTypes = listOf("Credit Card", "Debit Card", "Net Banking", "UPI")
            val paymentStatuses = listOf("Pending", "Completed", "Failed")

            // Payment Type Dropdown
            DropdownMenuField(
                label = "Payment Type",
                options = paymentTypes,
                selectedOption = selectedPaymentType,
                onOptionSelected = { selectedPaymentType = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp) // Using weight to fill space evenly
            )

            // Payment Status Dropdown
            DropdownMenuField(
                label = "Payment Status",
                options = paymentStatuses,
                selectedOption = selectedPaymentStatus,
                onOptionSelected = { selectedPaymentStatus = it },
                modifier = Modifier.weight(1f) // Fill remaining space
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Place Order Button
        Button(
            onClick = {
                orderViewModel.placeOrder(order) { createdOrder ->
                    if (createdOrder != null) {
                        mainViewModel.showToast("Order created!!")
                    } else {
                        mainViewModel.showToast("Order creation failed")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text("Confirm Place Order", color = Color.White)
        }
    }
}

@Composable
fun ProductDetailRow(cartItem: CartItem) {
    var showFullNameDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${cartItem.product.name}",
            fontSize = 16.sp,
            modifier = Modifier
                .width(200.dp) // Set a fixed width
                .clickable { showFullNameDialog = true }, // Show dialog on click
            overflow = TextOverflow.Ellipsis, // Ellipsis for overflow
            maxLines = 1 // Limit to one line
        )
        Text(
            text = "  x${cartItem.quantity}",
            fontSize = 16.sp
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Size: ${cartItem.size}",
            fontSize = 16.sp
        )
    }

    // Dialog to show full product name
    if (showFullNameDialog) {
        AlertDialog(
            onDismissRequest = { showFullNameDialog = false },
            title = { Text(text = "Product Name") },
            text = { Text(text = cartItem.product.name) },
            confirmButton = {
                Button(onClick = { showFullNameDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun DropdownMenuField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier // Allowing modifier to be passed
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(bottom = 16.dp)) { // Added bottom padding
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Box {
            Text(
                text = if (selectedOption.isEmpty()) "Select $label" else selectedOption,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                    .padding(8.dp),
                color = Color.DarkGray
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }) {
                        Text(text = option)
                    }
                }
            }
        }
    }
}

package com.kloc.unistore.screens

import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.kloc.unistore.entity.order.Attributes
import com.kloc.unistore.entity.order.Billing
import com.kloc.unistore.entity.order.Image
import com.kloc.unistore.entity.order.LineItem
import com.kloc.unistore.entity.order.MetaDataX
import com.kloc.unistore.entity.order.Order
import com.kloc.unistore.entity.order.OrderMetaData
import com.kloc.unistore.entity.order.Element
import com.kloc.unistore.entity.order.MetaDataSubElement
import com.kloc.unistore.entity.order.Shipping
import com.kloc.unistore.entity.order.StampData
import com.kloc.unistore.entity.order.TMCardEpoData
import com.kloc.unistore.entity.order.TaxLine
import com.kloc.unistore.entity.order.Taxe
import com.kloc.unistore.entity.order.TmcpPostFields
import com.kloc.unistore.model.orderViewModel.OrderViewModel
import com.kloc.unistore.model.productViewModel.ProductViewModel
import com.kloc.unistore.model.schoolViewModel.SchoolViewModel
import com.kloc.unistore.model.viewModel.MainViewModel
import com.kloc.unistore.navigation.Screen
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Popup
import com.kloc.unistore.entity.pineLabs.billing.AdditionalInfo
import com.kloc.unistore.entity.pineLabs.billing.UploadBilledTransaction
import com.kloc.unistore.entity.pineLabs.status.GetCloudBasedTxnStatus
import com.kloc.unistore.model.paymentViewModel.PaymentViewModel
import com.kloc.unistore.util.Constants
import kotlinx.coroutines.delay

@Composable
fun OrderDetailsScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    productViewModel: ProductViewModel,
    orderViewModel: OrderViewModel = hiltViewModel(),
    schoolViewModel: SchoolViewModel = hiltViewModel(),
    paymentViewModel: PaymentViewModel = hiltViewModel()

) {
    var paymentInitiate by remember { mutableStateOf(false)  }
    val orderCreatedBy by remember { mutableStateOf("Avinash")  }
    val schoolDetails by schoolViewModel.schoolDetails.collectAsState()
    val productDetails by productViewModel.productDetails.collectAsState()
    val cartItems by mainViewModel.cartViewModel.cartItems.collectAsState()
    val studentDetails by mainViewModel.studentViewModel.studentDetails.collectAsState()
    val totalAmount = cartItems.sumOf { it.product.price * it.quantity }
    val totalQuantity=cartItems.sumOf { it.quantity }
    var selectedPaymentType by remember { mutableStateOf(0) }
    var selectedPaymentStatus by remember { mutableStateOf("") }
    val MetaDataMap: HashMap<String, StampData> = hashMapOf()

    BackHandler {
        navController.navigate(Screen.CartScreen.route) {
            // Pop all the backstack to ensure CartScreen is the root
            popUpTo(Screen.CartScreen.route) { inclusive = true }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
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
                        Log.d("debug","${cartItem.itemId} ---${cartItem.variationId}---${cartItem.type}---${cartItem.size}")
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
                text = studentDetails?.shipingAddress ?: "969 Market",
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

                // Payment Type Dropdown
                DropdownMenuField(
                    label = "Payment Type",
                    selectedOption = paymentMode(selectedPaymentType),
                    onOptionSelected = { selectedPaymentType = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp) // Using weight to fill space evenly
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Place Order Button
            Button(
                onClick = {
                    paymentInitiate = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("Confirm Place Order", color = Color.White)
            }
        }
        // Overlay and Payment Processing Indicator
        if (paymentInitiate) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(enabled = false) {}, // Disable clicks on the background
                contentAlignment = Alignment.Center
            ) {
                PaymentProcessingIndicator(mainViewModel, paymentViewModel, productViewModel, orderViewModel, totalAmount, selectedPaymentType) { paymentInitiate = false }
            }
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
    selectedOption: String,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(bottom = 16.dp)) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Box {
            Text(
                text = selectedOption.ifEmpty { "Select $label" },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                    .padding(8.dp),
                color = Color.DarkGray
            )
            if (expanded) {
                Popup(
                    onDismissRequest = { expanded = false }
                ) {
                    Box(
                        modifier = Modifier
                            .width(200.dp) // Set dropdown width
                            .background(Color.White, shape = RoundedCornerShape(4.dp))
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .heightIn(max = 300.dp) // Limit height to make it scrollable
                        ) {
                            items((0..46).filter { paymentMode(it) != "Unknown" }) { mode ->
                                Text(
                                    text = paymentMode(mode),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onOptionSelected(mode)
                                            expanded = false
                                        }
                                        .padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun PaymentProcessingIndicator(
    mainViewModel: MainViewModel,
    paymentViewModel: PaymentViewModel,
    productViewModel: ProductViewModel,
    orderViewModel: OrderViewModel,
    totalAmount: Int,
    selectedPaymentType: Int,
    onCancel: () -> Unit
) {
    var paymentInitiate by remember { mutableStateOf(false)  }
    var isPolling by remember { mutableStateOf(false) }
    var pollingAttempts by remember { mutableStateOf(0) }
    val maxPollingAttempts = 10
    val pollingDelayMillis = 5000L
    val paymentResponse by paymentViewModel.paymentResponse.collectAsState()
    val paymentStatus by paymentViewModel.transactionStatus.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    LaunchedEffect(paymentInitiate) {
        if (paymentInitiate) {
            val paymentRequest = UploadBilledTransaction(
                TransactionNumber = "MP${(1000000000..9999999999L).random()}",
                SequenceNumber = Constants.SEQUENCE_NUMBER,
                AllowedPaymentMode = selectedPaymentType.toString(),
                Amount = totalAmount,
                UserID = Constants.USER_ID,
                MerchantID = Constants.MERCHANT_ID,
                SecurityToken = Constants.SECURITY_TOKEN,
                StoreID = Constants.STORE_ID,
                ClientID = Constants.CLIENT_ID,
                AutoCancelDurationInMinutes = 5,
                TotalInvoiceAmount = totalAmount,
                AdditionalInfo = listOf(
                    AdditionalInfo(Tag = "1001", Value = "XYZ"),
                    AdditionalInfo(Tag = "1002", Value = "ABC")
                )
            )
            paymentViewModel.initiatePayment(paymentRequest)
        }
    }
    LaunchedEffect(paymentResponse) {
        paymentResponse?.let { response ->
            if (response.ResponseCode == 0) {
                isPolling = true
                pollingAttempts = 0
                while (isPolling && pollingAttempts < maxPollingAttempts) {
                    paymentViewModel.getTransactionStatus(
                        GetCloudBasedTxnStatus(
                            MerchantID = Constants.MERCHANT_ID,
                            SecurityToken = Constants.SECURITY_TOKEN,
                            ClientID = Constants.CLIENT_ID,
                            UserID = Constants.USER_ID,
                            StoreID = Constants.STORE_ID,
                            PlutusTransactionReferenceID = response.PlutusTransactionReferenceID
                        )
                    )
                    delay(pollingDelayMillis)
                    pollingAttempts++
                }
            } else {
                mainViewModel.showToast("Payment failed")
                paymentInitiate = false
                onCancel()
            }
        }
    }
    LaunchedEffect(paymentStatus) {
        paymentStatus?.let { status ->
            if (status.ResponseCode == 0) {
                mainViewModel.showToast("Payment Successful.")
                orderViewModel.placeOrder(getOrderDetails(mainViewModel, productViewModel, selectedPaymentType)) {
                    mainViewModel.showToast(if (it != null) "Order Created!" else "Order Creation Failed")
                }
            } else if (status.ResponseCode == 1001 && pollingAttempts >= maxPollingAttempts) {
                mainViewModel.showToast("Timed out.")
            } else {
                mainViewModel.showToast("Payment Failed.")
            }
            isPolling = false
            paymentInitiate = false
            onCancel()
        }
    }

    if (paymentStatus?.ResponseCode == 0) {
        var startAnimation by remember { mutableStateOf(false) }

        val scale by animateFloatAsState(
            targetValue = if (startAnimation) 1f else 0f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing), label = ""
        )

        val checkmarkAlpha by animateFloatAsState(
            targetValue = if (startAnimation) 1f else 0f,
            animationSpec = tween(durationMillis = 600, delayMillis = 600, easing = FastOutSlowInEasing),
            label = ""
        )

        val glow by animateFloatAsState(
            targetValue = if (startAnimation) 1.2f else 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ), label = ""
        )

        val messageAlpha by animateFloatAsState(
            targetValue = if (startAnimation) 1f else 0f,
            animationSpec = tween(durationMillis = 600, delayMillis = 1200, easing = LinearEasing),
            label = ""
        )

        LaunchedEffect(Unit) {
            startAnimation = true
            delay(3000)
            onCancel()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Canvas(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(scale)
                    ) {
                        // Background Circle with a glowing effect
                        drawCircle(
                            color = Color.Green.copy(alpha = 0.3f),
                            radius = size.minDimension / 2 * glow,
                            style = Fill
                        )
                        // Main Checkmark Circle
                        drawCircle(
                            color = Color.Green,
                            radius = size.minDimension / 2,
                            style = Fill
                        )
                        if (checkmarkAlpha > 0f) {
                            // Animated Checkmark
                            drawLine(
                                color = Color.White.copy(alpha = checkmarkAlpha),
                                start = Offset(size.width * 0.3f, size.height * 0.6f),
                                end = Offset(size.width * 0.45f, size.height * 0.75f),
                                strokeWidth = 8f
                            )
                            drawLine(
                                color = Color.White.copy(alpha = checkmarkAlpha),
                                start = Offset(size.width * 0.45f, size.height * 0.75f),
                                end = Offset(size.width * 0.7f, size.height * 0.4f),
                                strokeWidth = 8f
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Payment Successful Text with a smooth fade-in
                if (messageAlpha > 0f) {
                    BasicText(
                        text = "Payment Successful!",
                        style = TextStyle(
                            color = Color.White.copy(alpha = messageAlpha),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(100.dp)) {
                        drawCircle(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            radius = size.minDimension / 2,
                            style = Stroke(width = 6.dp.toPx())
                        )
                        drawCircle(
                            color = Color.Blue.copy(alpha = 0.8f),
                            radius = size.minDimension / 2 * scale,
                            style = Stroke(width = 6.dp.toPx())
                        )
                    }

                    BasicText(
                        text = "🛍️",
                        style = TextStyle(fontSize = 30.sp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                BasicText(
                    text = "Processing Payment...",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        isPolling = false
                        paymentInitiate = false
                        onCancel()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(text = "Cancel Payment", color = Color.White)
                }
            }
        }
    }
}


fun paymentMode(mode: Int): String {
    val paymentModes = mapOf(
        0 to "Allow all modes",
        1 to "Card",
        2 to "Cash",
        3 to "Points",
        4 to "Wallets",
        6 to "Brand EMI",
        7 to "Sodexo",
        8 to "PhonePe",
        9 to "UPI PayTm",
        10 to "UPI Sale",
        11 to "UPI Bharat QR",
        12 to "Airtel Bank",
        19 to "Paper POS",
        20 to "Bank EMI",
        21 to "Amazon Pay via Mobile No., QR and Barcode",
        22 to "Sale with/Without Instant Discount",
        23 to "Sale Cardless Bank EMI (ICICI & Federal Bank)",
        24 to "Sale Cardless Brand EMI (ICICI & Federal Bank)",
        35 to "NBFC Product Sale",
        37 to "myEMI",
        39 to "Epaylater",
        40 to "NTB (New To Business)",
        42 to "Zomato Pay",
        44 to "STELLR POR",
        45 to "STELLR POSA"
    )
    return paymentModes[mode]?:"Unknown"
}

fun getOrderDetails(
    mainViewModel: MainViewModel,
    productViewModel: ProductViewModel,
    selectedPaymentType: Int): Order {
    val stampDataMap: Map<String, StampData> =
        mainViewModel.cartViewModel.cartItems.value?.associateBy(
            { it.itemId.toString() } // Key: Product ID as String
        ) { cartItem ->
            StampData(
                product_id = cartItem.product.id, // Product ID
                quantity = cartItem.quantity, // Quantity
                attributes = Attributes(
                    attribute_class = cartItem.product.categories.firstOrNull()?.name.orEmpty(), // Class name
                    attribute_pa_color = "", // Selected color
                    attribute_pa_size = if (cartItem.type.equals(
                            "Size",
                            ignoreCase = true
                        )
                    ) cartItem.size else "",
                    attribute_pa_custom_size = if (cartItem.type.equals(
                            "Custom",
                            ignoreCase = true
                        )
                    ) cartItem.size else ""
                ),
                variation_id = "${cartItem.variationId}", // Variation ID
                discount = "" // Discount
            )
        } ?: hashMapOf()
    val tmCardEpoDataList = listOf(
        TMCardEpoData(
            mode = "builder",
            cssclass = "",
            hideLabelInCart = "",
            hideValueInCart = "",
            hideLabelInOrder = "",
            hideValueInOrder = "",
            element = Element(
                type = "textfield",
                rules = listOf(listOf("")),
                rulesType = listOf(listOf("")),
                _metaDataSubElement = MetaDataSubElement(priceType = false)
            ),
            name = "Student Name",
            value = mainViewModel.studentViewModel.studentDetails.value?.studentName.orEmpty(),
            price = 0,
            section = "5a4f42259ec412.74128636",
            sectionLabel = "Student Name",
            percentCurrentTotal = 0,
            fixedCurrentTotal = 0,
            currencies = emptyList(),
            pricePerCurrency = emptyList(),
            quantity = 1,
            keyId = 0,
            keyValueId = 0
        ),
        TMCardEpoData(
            mode = "builder",
            cssclass = "",
            hideLabelInCart = "",
            hideValueInCart = "",
            hideLabelInOrder = "",
            hideValueInOrder = "",
            element = Element(
                type = "textfield",
                rules = listOf(listOf("")),
                rulesType = listOf(listOf("")),
                _metaDataSubElement = MetaDataSubElement(priceType = false)
            ),
            name = "Parent Name",
            value = mainViewModel.studentViewModel.studentDetails.value?.parentName.orEmpty(),
            price = 0,
            section = "5a4f41279132c8.70932947",
            sectionLabel = "Parent Name",
            percentCurrentTotal = 0,
            fixedCurrentTotal = 0,
            currencies = emptyList(),
            pricePerCurrency = emptyList(),
            quantity = 1,
            keyId = 0,
            keyValueId = 0
        ),
        TMCardEpoData(
            mode = "builder",
            cssclass = "",
            hideLabelInCart = "",
            hideValueInCart = "",
            hideLabelInOrder = "",
            hideValueInOrder = "",
            element = Element(
                type = "textfield",
                rules = listOf(listOf("")),
                rulesType = listOf(listOf("")),
                _metaDataSubElement = MetaDataSubElement(priceType = false)
            ),
            name = "New Class",
            value = mainViewModel.studentViewModel.studentDetails.value?.selectedClass.orEmpty(),
            price = 0,
            section = "5a4f42529ec430.43177555",
            sectionLabel = "New Class",
            percentCurrentTotal = 0,
            fixedCurrentTotal = 0,
            currencies = emptyList(),
            pricePerCurrency = emptyList(),
            quantity = 1,
            keyId = 0,
            keyValueId = 0
        ),
        TMCardEpoData(
            mode = "builder",
            cssclass = "",
            hideLabelInCart = "",
            hideValueInCart = "",
            hideLabelInOrder = "",
            hideValueInOrder = "",
            element = Element(
                type = "textfield",
                rules = listOf(listOf("")),
                rulesType = listOf(listOf("")),
                _metaDataSubElement = MetaDataSubElement(priceType = false)
            ),
            name = "School ID",
            value = mainViewModel.cartViewModel.cartItems.value[0].product.categories.last().slug,
            price = 0,
            section = "5a4f423c9ec421.55952783",
            sectionLabel = "School ID",
            percentCurrentTotal = 0,
            fixedCurrentTotal = 0,
            currencies = emptyList(),
            pricePerCurrency = emptyList(),
            quantity = 1,
            keyId = 0,
            keyValueId = 0
        ),
        TMCardEpoData(
            mode = "builder",
            cssclass = "",
            hideLabelInCart = "",
            hideValueInCart = "",
            hideLabelInOrder = "",
            hideValueInOrder = "",
            element = Element(
                type = "select",
                rules = mapOf("M_0" to listOf(""), "F_1" to listOf("")),
                rulesType = mapOf("M_0" to listOf(""), "F_1" to listOf("")),
                _metaDataSubElement = MetaDataSubElement(priceType = false)
            ),
            name = "Gender",
            value = mainViewModel.studentViewModel.studentDetails.value?.gender.orEmpty(),
            price = 0,
            section = "5a4f427f9ec452.18208379",
            sectionLabel = "Gender",
            percentCurrentTotal = 0,
            fixedCurrentTotal = 0,
            currencies = emptyList(),
            pricePerCurrency = emptyList(),
            quantity = 1,
            keyId = 0,
            keyValueId = 0
        )
    )
    val individualLineItems: List<LineItem> = mainViewModel.cartViewModel.cartItems.value.map { cartItem ->
        LineItem(
            bundled_by = "",
            bundled_item_title = "",
            bundled_items = emptyList(),
            image = Image(
                src = cartItem.product.images?.firstOrNull()?.src
                    ?: "default_image_url" // Use the `src` from `cartItem.image`
            ),
            meta_data = listOf(
                OrderMetaData(
                    id = 1,
                    key = "_bundled_item_id",
                    value = "${cartItem.itemId}"
                ),
                OrderMetaData(
                    id = 3,
                    key = if (cartItem.type == "Custom") "customSize" else "pa_size",
                    value = "${cartItem.size}"
                ),
                OrderMetaData(
                    id = 3,
                    key = "_stamp",
                    value = stampDataMap
                )
            ),
            name = cartItem.product.name,
            parent_name = mainViewModel.studentViewModel.studentDetails.value?.parentName.orEmpty(),
            price = cartItem.product.price,
            product_id = cartItem.product.id,
            quantity = cartItem.quantity,
            sku = "",
            subtotal = "0.00",
            subtotal_tax = "0.00",
            taxes = listOf(
                Taxe(
                    id = 1,
                    subtotal = "0.00",
                    total = "0.00"
                )
            ),
            total = "${cartItem.product.price * cartItem.quantity}",
            total_tax = "0.00",
            variation_id = cartItem.variationId
        )
    }
//    Log.d("debug", "${mainViewModel.cartViewModel.cartItems.value.sumOf { it.product.price * it.quantity }}")
    // Convert CartItem to LineItem
    val lineItems: List<LineItem> = listOf(
        LineItem(
            bundled_by = "",
            bundled_item_title = "",
            bundled_items = emptyList(),
            image = Image(
//                id = cartItem.product.images.first().id,  //  line_items[0][image][id] is not of type integer.
                src = productViewModel.productDetails.value?.images?.firstOrNull()?.src
                    ?: "default_image_url"
            ),
            meta_data = listOf(
                OrderMetaData(
                    id = 1,
                    key = "_tmdata",
                    value = listOf(
                        TmcpPostFields(
                            tmcp_textfield_0 = mainViewModel.studentViewModel.studentDetails.value?.studentName.orEmpty(),
                            tmcp_textfield_1 = mainViewModel.studentViewModel.studentDetails.value?.parentName.orEmpty(),
                            tmcp_textfield_2 = mainViewModel.studentViewModel.studentDetails.value?.selectedClass.orEmpty(),
                            tmcp_textfield_3 = mainViewModel.cartViewModel.cartItems.value[0].product.categories.last().slug,
                            tmcp_select_4 = mainViewModel.studentViewModel.studentDetails.value?.gender.orEmpty()
                        )
                    )
                ),
                OrderMetaData(
                    id = 3,
                    key = "_stamp",
                    value = stampDataMap
                ),
                OrderMetaData(
                    id = 4,
                    key = "_tmcartepo_data",
                    value = tmCardEpoDataList
                )
            ),
            name = productViewModel.productDetails.value?.name ?: "Product A",
            parent_name = mainViewModel.studentViewModel.studentDetails.value?.parentName.orEmpty(),
            price = mainViewModel.cartViewModel.cartItems.value.sumOf { it.product.price * it.quantity },
            product_id = productViewModel.productDetails.value?.id
                ?: 0,  // Set product_id from CartItem
            quantity = 1,  // Add the total quantity quantity here
            sku = "",
            subtotal = "0.00",
            subtotal_tax = "0.00",
//            tax_class = "standard",     // Error
            taxes = listOf(
                Taxe(
                    id = 1,
                    subtotal = "0.00", // Replacing 'amount' with 'subtotal'
                    total = "0.00"    // Providing a value for 'total'
                )
            ),
            total = "0.0",
            total_tax = "0.00",
            variation_id = 0
        )
    )
    return Order(

        billing = Billing(
            //TODO: Data Attached
            first_name = mainViewModel.studentViewModel.studentDetails.value?.studentName?.split(" ")?.getOrNull(0) ?: "John",
            last_name = mainViewModel.studentViewModel.studentDetails.value?.studentName?.split(" ")?.getOrNull(1) ?: "Doe",
            address_1 = mainViewModel.studentViewModel.studentDetails.value?.billingAddress ?: "969 Market",
            address_2 = "",
            city = mainViewModel.studentViewModel.studentDetails.value?.city ?: "San Francisco",
            state = mainViewModel.studentViewModel.studentDetails.value?.state ?: "CA",
            postcode = mainViewModel.studentViewModel.studentDetails.value?.zipCode ?: "94103",
            email = mainViewModel.studentViewModel.studentDetails.value?.emailAddress ?: "john.doe@example.com",
            phone = mainViewModel.studentViewModel.studentDetails.value?.phoneNumber ?: "(555) 555-5555",
            country = "India",                          //TODO: Hard-coded
            company = "Kloc Technologies",              //TODO: Hard-coded
        ),
        cart_hash = "abc123",
        cart_tax = "5.00",
        coupon_lines = emptyList(),
        created_via = "web",
        currency = "INR",
        currency_symbol = "₹",
        customer_id = 101,
        customer_ip_address = "192.168.1.1",
        customer_note = "Leave at the front door.",
        customer_user_agent = "",
        date_completed = null,
        date_completed_gmt = null,
        date_created = "2024-11-21T10:00:00Z",
        date_created_gmt = "2024-11-21T10:00:00Z",
        date_modified = "2024-11-22T12:00:00Z",
        date_modified_gmt = "2024-11-22T12:00:00Z",
        date_paid = "2024-11-21T10:15:00Z",
        date_paid_gmt = "2024-11-21T10:15:00Z",
        discount_tax = "0.00",
        discount_total = "10.00",
        fee_lines = emptyList(),
//        id = 1001,
        is_editable = false,
        line_items = lineItems + individualLineItems,
        meta_data = listOf(
            MetaDataX(
                id = 1,
                key = "",
                value = ""
            )
        ),
        needs_payment = false,
        needs_processing = true,
        number = "ORD12345",
        order_key = "orderkey123",
        parent_id = 0,
        payment_method = paymentMode(selectedPaymentType),                //TODO:  Payment Method Attached
        payment_method_title = "Direct Bank Transfer",       //TODO: Hard-Coded
        payment_url = "https://payment.example.com",
        prices_include_tax = true,
        refunds = emptyList(),
        shipping = Shipping(
            //TODO: Data Attached
            first_name = mainViewModel.studentViewModel.studentDetails.value?.studentName?.split(" ")?.getOrNull(0) ?: "John",
            last_name = mainViewModel.studentViewModel.studentDetails.value?.studentName?.split(" ")?.getOrNull(1) ?: "Doe",
            address_1 = mainViewModel.studentViewModel.studentDetails.value?.shipingAddress ?: "969 Market",
            address_2 = "",
            city = mainViewModel.studentViewModel.studentDetails.value?.city ?: "San Francisco",
            state = mainViewModel.studentViewModel.studentDetails.value?.state ?: "CA",
            postcode = mainViewModel.studentViewModel.studentDetails.value?.zipCode ?: "94103",
            phone = mainViewModel.studentViewModel.studentDetails.value?.phoneNumber ?: "(555) 555-5555",
            country = "India",                              //TODO: Hard-Coded
            company = "Kloc Technologies",                  //TODO: Hard-Coded
        ),
        shipping_lines = emptyList(),
        shipping_tax = "0.00",
        shipping_total = "5.00",
        status = "completed",
        tax_lines = listOf(
            TaxLine(
                compound = false,
                id = 1,
                label = "Tax",
                meta_data = emptyList(),
                rate_code = "TAX_5",
                rate_id = 101,
                rate_percent = 5.0,
                shipping_tax_total = "0.00",
                tax_total = "1.25"
            )
        ),
        total = "${mainViewModel.cartViewModel.cartItems.value.sumOf { it.product.price * it.quantity }}",
        total_tax = "1.25",
        transaction_id = "txn12345",
        version = "1.0"
    )
}


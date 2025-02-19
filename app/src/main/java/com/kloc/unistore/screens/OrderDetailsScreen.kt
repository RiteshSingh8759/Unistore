package com.kloc.unistore.screens

import java.util.UUID
import android.util.Log
import kotlinx.coroutines.delay
import androidx.navigation.NavController
import kotlin.time.Duration.Companion.milliseconds
import androidx.hilt.navigation.compose.hiltViewModel
import com.dokar.sonner.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.kloc.unistore.common.*
import com.kloc.unistore.util.Constants
import com.kloc.unistore.navigation.Screen
import com.kloc.unistore.model.viewModel.MainViewModel
import com.kloc.unistore.model.orderViewModel.OrderViewModel
import com.kloc.unistore.model.paymentViewModel.*
import com.kloc.unistore.entity.order.*
import com.kloc.unistore.entity.cart.CartItem
import com.kloc.unistore.entity.order.TmcpData
import com.kloc.unistore.entity.pineLabs.billing.*
import com.kloc.unistore.entity.pineLabs.status.GetCloudBasedTxnStatus
import com.kloc.unistore.firestoredb.viewmodel.EmployeeViewModel
import com.kloc.unistore.model.productViewModel.ProductViewModel

@Composable
fun OrderDetailsScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    productViewModel: ProductViewModel,
    employeeViewModel: EmployeeViewModel,
    orderViewModel: OrderViewModel = hiltViewModel(),
    paymentViewModel: PaymentViewModel = hiltViewModel()
) {
    val toaster = rememberToasterState()
    Toaster(state = toaster, darkTheme = true, maxVisibleToasts = 1)

    var paymentInitiate by remember { mutableStateOf(false) }
    val cartItems by mainViewModel.cartViewModel.cartItems.collectAsState()
    val studentDetails by mainViewModel.studentViewModel.studentDetails.collectAsState()
    // Compute total amount safely
    var totalAmount by remember { mutableStateOf(0.0) }
    var priceCollections by remember { mutableStateOf(mutableMapOf<String, Pair<String, Int>>()) }

    // Fetch product variations for all cart items, regardless of LazyColumn visibility
    LaunchedEffect(cartItems) {
        cartItems.forEachIndexed { index, cartItem ->
            val sizeToPass = if (cartItem.customSize) "choose from above" else cartItem.size
            productViewModel.fetchProductVariations(
                cartItem.product.id,
                index,
                cartItem.grade,
                cartItem.color,
                sizeToPass
            )
        }
    }

    val productVariationMap by productViewModel.productVariations.collectAsState()

    // Update price collections when product variations change
    LaunchedEffect(productVariationMap) {
        val updatedPriceCollections = priceCollections.toMutableMap()

        cartItems.forEachIndexed { index, cartItem ->
            val variation = productVariationMap[index]

            // Create a conditional variationKey based on available attributes
            val variationKey = buildString {
                append(cartItem.product.id) // Always include item ID
                if(cartItem.size.isNotEmpty() || cartItem.color.isNotEmpty() || cartItem.grade.isNotEmpty()) {
                    if (!cartItem.size.isNullOrEmpty()) append("-${cartItem.size}")
                    if (!cartItem.color.isNullOrEmpty()) append("-${cartItem.color}")
                    if (!cartItem.grade.isNullOrEmpty()) append("-${cartItem.grade}")
                }
                else {
                    append("${cartItem.product.name}") // Ensure this key exists for products without variations
                }
            }

            if (variation != null) {
                updatedPriceCollections[variationKey] = Pair(
                    variation.regular_price.toString(),
                    cartItem.quantity
                )
            } else {
                // Handle cases where there are no variations (like for the belt)
                updatedPriceCollections[variationKey] = Pair(
                    cartItem.product.regular_price.toString(), // Use base price for products like belt
                    cartItem.quantity
                )
            }
        }

        priceCollections = updatedPriceCollections
        // Calculate total amount based on all items in priceCollections
        totalAmount = priceCollections.values.sumOf { (price, quantity) ->
            price.toDoubleOrNull()?.times(quantity) ?: 0.0
        }
    }



    val totalQuantity = cartItems.sumOf { it.quantity }
    var selectedPaymentType by remember { mutableStateOf(0) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)) {
            OrderSectionCard(title = "Products") {
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(cartItems.size) { index ->
                        val cartItem = cartItems[index]
                        if(productVariationMap.isNotEmpty()){
                            val productVariationList = productVariationMap[index]
                            if (productVariationList != null) {
                                cartItem.price = productVariationList.regular_price.toString()
                                cartItem.variationId= productVariationList.id!!
                                cartItem.sku= productVariationList.sku.toString()
                            }
                        }
                        ProductDetailItem(cartItem)
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            thickness = 1.dp, // Specify the thickness of the divider
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            OrderSectionCard(title = "Shipping Address") {
                val fullAddress = buildString {
                    appendLine("Line 1: ${studentDetails?.billingAddressLine1 ?: "Not provided"}")
                    studentDetails?.billingAddressLine2?.takeIf { it.isNotBlank() }?.let {
                        appendLine("Line 2: $it")
                    }
                    appendLine("City: ${studentDetails?.billingCity ?: "Not provided"}")
                    appendLine("State: ${studentDetails?.billingState ?: "Not provided"}")
                    appendLine("Zip Code: ${studentDetails?.billingZipCode ?: "Not provided"}")
                }
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    item {
                        Text(
                            text = fullAddress.ifEmpty { "No billing address provided" },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            OrderSectionCard(title = "Order Summary") {
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Total Items", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "$totalQuantity", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Total Amount", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            Text(text = "₹$totalAmount", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            OrderSectionCard(title = "Payment Details") {
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    item {
                        CommonDropdownMenu(
                            label = "Payment Type",
                            items = (0..46).filter { paymentMode(it) != "Unknown" }.map { paymentMode(it) },
                            selectedItem = paymentMode(selectedPaymentType),
                            onItemSelected = { selectedOption ->
                                selectedPaymentType = (0..46).first { paymentMode(it) == selectedOption }
                            },
                            leadingIcon = { Icon(Icons.Rounded.Payment, null) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { paymentInitiate = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Confirm Your Order", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
        if (paymentInitiate) {
            Box(modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = false) {}, contentAlignment = Alignment.Center) {
                if (totalAmount < 100) {
                    toaster.show(Toast(message = "Payment amount should be atleast 1 rupees", type = ToastType.Error, duration = 2000.milliseconds))
                } else {
                    val deviceDetails =employeeViewModel.deviceDetails.value.data?.device
                    PaymentProcessingIndicator(
                        mainViewModel,
                        paymentViewModel,
                        productViewModel,
                        orderViewModel,
                        totalAmount,
                        selectedPaymentType,
                        onComplete = {
                            paymentViewModel.resetPaymentData()
                            paymentInitiate = false },
                        onCancel = {
                            paymentViewModel.resetPaymentData()
                            paymentViewModel.cancelPayment(
                                GetCloudBasedTxnStatus(
                                UserID = deviceDetails?.user_id,
                                MerchantID = deviceDetails?.merchant_id,
                                SecurityToken = deviceDetails?.security_token,
                                StoreID = deviceDetails?.store_id,
                                ClientID = deviceDetails?.device_id,
                                PlutusTransactionReferenceID = mainViewModel.ptrnNumber,
                                amount = totalAmount
                            ))
                            paymentInitiate = false },
                        employeeViewModel,
                        navController
                    )
                }
            }
        }
    }
}

@Composable
fun OrderSectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(bottom = 12.dp))
            content()
        }
    }
}

@Composable
fun ProductDetailItem(cartItem: CartItem) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = cartItem.product.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                fontSize = 15.sp,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis
            )
            if (cartItem.size.trim().isNotEmpty()) {
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
                                text = cartItem.size,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
            if (cartItem.color.trim().isNotEmpty()) {
                Text(
                    text = "Color: ${cartItem.color}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            if (cartItem.grade.trim().isNotEmpty()) {
                Text(
                    text = "Grade: ${cartItem.grade}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End, modifier = Modifier.wrapContentWidth()) {
            Text(
                text = "x${cartItem.quantity}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 8.dp)
            )
            if (cartItem.price.isNotBlank()){
                Text(
                    text = "₹${cartItem.price.toDouble() * cartItem.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            else if (cartItem.size.isEmpty() && cartItem.grade.isEmpty() && cartItem.color.isEmpty()) {
                Text(
                    text = "₹${cartItem.product.regular_price.toDouble() * cartItem.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
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
    totalAmount: Double,
    selectedPaymentType: Int,
    onComplete: () -> Unit,
    onCancel: () -> Unit,
    employeeViewModel: EmployeeViewModel,
    navController: NavController,
) {
    val toaster = rememberToasterState()
    Toaster(state = toaster, darkTheme = true, maxVisibleToasts = 1)
    var ptrnNumber by remember { mutableStateOf(0) }
    var createOrder by remember { mutableStateOf(false) }
    var pollingAttempts by remember { mutableStateOf(0) }
    val maxPollingAttempts = 12
    val pollingDelayMillis = 5000L
    val paymentResponse by paymentViewModel.paymentResponse.collectAsState()
    val paymentStatus by paymentViewModel.transactionStatus.collectAsState()
    var orderId by remember { mutableStateOf(0) }
    val transactionNumber by remember { mutableStateOf(generateTransactionNumber()) }
    val waitingTime by remember { mutableStateOf(when (selectedPaymentType) {
        1 -> 2
        10 -> 5
        else -> 2
    }) }
    var remainingTime by remember { mutableStateOf(waitingTime * 60) }
    var currentAnimation by remember { mutableStateOf("Payment Processing") }
    val deviceDetails =employeeViewModel.deviceDetails.value.data?.device
    // Showing Animations
    when (currentAnimation) {
        "Payment Processing" -> CommonProgressIndicator(message = "Processing Payment\nRemaining Time: ${String.format("%02d:%02d", remainingTime / 60, remainingTime % 60)}", buttonName = "Cancel Payment", dotAnimation = false) { onCancel() }
        "Payment Successful Creating Order" -> CommonProgressIndicator("Payment Successful!\nCreating Order")
        "Payment Successful Order Created" -> SuccessfulAnimation("Order Created!\nOrder Id: $orderId") {
            onComplete()
            navController.popBackStack(Screen.SchoolCategoryScreen.route, inclusive = false)
        }
        else -> onCancel()
    }
    // Creating Order And insuring order create once only
    if (createOrder) {
        LaunchedEffect(Unit) {
            orderViewModel.placeOrder(getOrderDetails(mainViewModel, productViewModel, selectedPaymentType, "${ptrnNumber}",employeeViewModel))
            { order ->
                if (order != null) {
                    if (orderId == 0 ) {
                        orderId = order.id ?: 0
                        currentAnimation = "Payment Successful Order Created"
                    }
                }
                else { toaster.show(Toast(message = "Order Creation Failed.", type = ToastType.Error, duration = 2000.milliseconds)) }
            }
        }
    } else {
        // Showing live remaining time for payment
        LaunchedEffect(remainingTime) {
            if (remainingTime > 0) {
                delay(1000L)
                remainingTime -= 1
            } else { onCancel() }
        }
        // Sending request to Pine Lab machine for payment
        LaunchedEffect(Unit) {
            val paymentRequest = UploadBilledTransaction(
                TransactionNumber = transactionNumber,
                SequenceNumber = Constants.SEQUENCE_NUMBER,
                AllowedPaymentMode = selectedPaymentType.toString(),
                Amount = totalAmount * 100,
                UserID = deviceDetails?.user_id,
                MerchantID = deviceDetails?.merchant_id,
                SecurityToken =deviceDetails?.security_token,
                StoreID = deviceDetails?.store_id,
                ClientID = deviceDetails?.device_id,
                AutoCancelDurationInMinutes = waitingTime,
                TotalInvoiceAmount = totalAmount,
                AdditionalInfo = listOf(
                    AdditionalInfo(Tag = "1001", Value = "XYZ"),
                    AdditionalInfo(Tag = "1002", Value = "ABC")
                )
            )
            paymentViewModel.initiatePayment(paymentRequest)
        }
        // Getting payment Response from Pine Lab machine
        LaunchedEffect(paymentResponse) {
            paymentResponse?.let { response ->
                mainViewModel.ptrnNumber = response.PlutusTransactionReferenceID ?: 0
                if (response.ResponseCode == 0) {
                    pollingAttempts = 0
                    while (pollingAttempts < maxPollingAttempts) {
                        paymentViewModel.getTransactionStatus(
                            GetCloudBasedTxnStatus(
                                UserID = deviceDetails?.user_id,
                                MerchantID = deviceDetails?.merchant_id,
                                SecurityToken =deviceDetails?.security_token,
                                StoreID = deviceDetails?.store_id,
                                ClientID = deviceDetails?.device_id,
                                PlutusTransactionReferenceID = mainViewModel.ptrnNumber,
                                amount = totalAmount
                            )
                        )
                        delay(pollingDelayMillis)
                        pollingAttempts++
                    }
                    if (paymentViewModel.transactionStatus.value?.ResponseCode == 1001 && pollingAttempts >= maxPollingAttempts) {
                        toaster.show(Toast(message = "Timed out.", type = ToastType.Warning, duration = 2000.milliseconds))
                        onCancel()
                    }
                } else {
                    toaster.show(Toast(message = "Payment failed", type = ToastType.Error, duration = 2000.milliseconds))
                    onCancel()
                }
            }
        }
        // Getting payment status from Pine Lab machine
        LaunchedEffect(paymentStatus) {
            paymentStatus?.let { status ->
                when (status.ResponseMessage.toString()) {
                    "TXN APPROVED" -> {
                        currentAnimation = "Payment Successful Creating Order"
                        createOrder = true
                    }
                    "PLEASE APPROVE OPEN TXN FIRST" -> {
                        toaster.show(Toast(message = "Please complete all previous transaction first", type = ToastType.Warning, duration = 2000.milliseconds))
                    }
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
//        3 to "Points",
//        4 to "Wallets",
//        6 to "Brand EMI",
//        7 to "Sodexo",
//        8 to "PhonePe",
//        9 to "UPI PayTm",
        10 to "UPI Sale",
//        11 to "UPI Bharat QR",
//        12 to "Airtel Bank",
//        19 to "Paper POS",
//        20 to "Bank EMI",
//        21 to "Amazon Pay via Mobile No., QR and Barcode",
//        22 to "Sale with/Without Instant Discount",
//        23 to "Sale Cardless Bank EMI (ICICI & Federal Bank)",
//        24 to "Sale Cardless Brand EMI (ICICI & Federal Bank)",
//        35 to "NBFC Product Sale",
//        37 to "myEMI",
//        39 to "Epaylater",
//        40 to "NTB (New To Business)",
//        42 to "Zomato Pay",
//        44 to "STELLR POR",
//        45 to "STELLR POSA"
    )
    return paymentModes[mode]?:"Unknown"
}

fun getOrderDetails(
    mainViewModel: MainViewModel,
    productViewModel: ProductViewModel,
    selectedPaymentType: Int,
    ptrnNumber: String,
    employeeViewModel: EmployeeViewModel
): Order {
    val stampDataMap: MutableMap<String, MutableList<StampData>> = mutableMapOf()
    val staffDetails=employeeViewModel.res1.value.data?.employee
    val cartItems = mainViewModel.cartViewModel.cartItems.value
    cartItems.forEach { cartItem ->
        val key = cartItem.itemId.toString()
        val stampData = StampData(
            product_id = cartItem.product.id, // Product ID
            quantity = cartItem.quantity, // Quantity
            attributes = Attributes(
                attribute_class = mainViewModel.className, // Class name
                attribute_pa_color =if( cartItem.color.isEmpty()) "" else cartItem.color.toString(), // Selected color
                attribute_pa_size = if (cartItem.type.equals("Size", ignoreCase = true)) cartItem.size else "",
                attribute_pa_custom_size = if (cartItem.type.equals("Custom", ignoreCase = true)) cartItem.size else ""
            ),
            variation_id = "${cartItem.variationId}", // Variation ID
            discount = "" // Discount
        )

        // Add the StampData to the list associated with the key
        if (stampDataMap.containsKey(key)) {
            stampDataMap[key]?.add(stampData)
        } else {
            stampDataMap[key] = mutableListOf(stampData)
        }
    }

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
    val totalOrderAmount = cartItems.sumOf { cartItem ->
        val itemPrice = cartItem.price.toDoubleOrNull() ?: cartItem.product.price
        itemPrice * cartItem.quantity
    }
    val individualLineItems: List<LineItem> = cartItems.map { cartItem ->
        val itemPrice = cartItem.price.toDoubleOrNull() ?: cartItem.product.price
        LineItem(
            bundled_by = "",
            bundled_item_title = "",
            bundled_items = emptyList(),
            image = Image(
                src = cartItem.product.images.firstOrNull()?.src
                    ?: "default_image_url" // Use the `src` from `cartItem.image`
            ),
            meta_data = listOf(
                OrderMetaData(
                    id = 1,
                    key = "_bundled_item_id",
                    value = "${cartItem.itemId}"
                ),
                OrderMetaData(
                    id = 2,
                    key = "pa_class",
                    value = mainViewModel.className
                ),
                OrderMetaData(
                    id = 2,
                    key = "pa_color",
                    value = cartItem.color
                ),
                OrderMetaData(
                    id = 3,
                    key = if (cartItem.type == "Custom") "customSize" else "pa_size",
                    value = cartItem.size
                ),
                OrderMetaData(
                    id = 3,
                    key = "_stamp",
                    value = stampDataMap
                )
            ).filter { it.value.toString().isNotBlank() },
            name = cartItem.product.name,
            parent_name = mainViewModel.studentViewModel.studentDetails.value?.parentName.orEmpty(),
            price = itemPrice,
            product_id = cartItem.product.id,
            quantity = cartItem.quantity,
            sku =cartItem.sku,
            subtotal = "0.00",
            subtotal_tax = "0.00",
            taxes = listOf(
                Taxe(
                    id = 1,
                    subtotal = "0.00",
                    total = "0.00"
                )
            ),
            total = "${itemPrice * cartItem.quantity}",
            total_tax = "0.00",
            variation_id = cartItem.variationId
        )
    }
    // Convert CartItem to LineItem
    val lineItems: MutableList<LineItem> = mutableListOf(
        LineItem(
            bundled_by = "",
            bundled_item_title = "",
            bundled_items = emptyList(),
            image = Image(
                src = productViewModel.productDetails.value?.images?.firstOrNull()?.src
                    ?: "default_image_url"
            ),
            meta_data = listOf(
                OrderMetaData(
                    id = 1,
                    key = "pa_class",
                    value = mainViewModel.className
                ),
                OrderMetaData(
                    id = 1,
                    key = "_tmdata",
                    value = listOf(
                      TmcpData( tmcp_post_fields= TmcpPostFields(
                          tmcp_textfield_0 = mainViewModel.studentViewModel.studentDetails.value?.studentName.orEmpty(),
                          tmcp_textfield_1 = mainViewModel.studentViewModel.studentDetails.value?.parentName.orEmpty(),
                          tmcp_textfield_2 = mainViewModel.studentViewModel.studentDetails.value?.selectedClass.orEmpty(),
                          tmcp_textfield_3 = mainViewModel.cartViewModel.cartItems.value[0].product.categories.last().slug,
                          tmcp_select_4 = mainViewModel.studentViewModel.studentDetails.value?.gender.orEmpty()
                      ))
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
            name = productViewModel.productDetails.value?.name ?: "Bundled Products",
            parent_name = mainViewModel.studentViewModel.studentDetails.value?.parentName.orEmpty(),
            price =totalOrderAmount,
            product_id = productViewModel.productDetails.value?.id
                ?: 0,  // Set product_id from CartItem
            quantity = 1,  // Add the total quantity quantity here
            sku = " ",
            subtotal = "0.00",
            subtotal_tax = "0.00",
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
    lineItems += individualLineItems
    return Order(

        billing = Billing(
            //TODO: Data Attached
            first_name = mainViewModel.studentViewModel.studentDetails.value?.studentName?.split(" ")?.getOrNull(0) ?: "",
            last_name = mainViewModel.studentViewModel.studentDetails.value?.studentName?.split(" ")?.getOrNull(1) ?: "",
            address_1 = mainViewModel.studentViewModel.studentDetails.value?.billingAddressLine1 ?: "",
            address_2 = mainViewModel.studentViewModel.studentDetails.value?.billingAddressLine2 ?: "",
            city = mainViewModel.studentViewModel.studentDetails.value?.billingCity ?: "",
            state = mainViewModel.studentViewModel.studentDetails.value?.billingState ?: "",
            postcode = mainViewModel.studentViewModel.studentDetails.value?.billingZipCode ?: "",
            email = mainViewModel.studentViewModel.studentDetails.value?.emailAddress ?: "",
            phone = mainViewModel.studentViewModel.studentDetails.value?.phoneNumber ?: "",
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
        customer_note = mainViewModel.studentViewModel.studentDetails.value?.note ?: "",
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
        is_editable = false,
        line_items = lineItems,
        meta_data = listOf(
            MetaDataX(
                id = 1,
                key = "staff_id",
                value = "${staffDetails?.id} ${staffDetails?.name}"
            ),
            MetaDataX(
                id = 2,
                key = "addressType",
                value = "${if(mainViewModel.typeOfAddress) "school delivery" else ""}"
            )
        ),
        needs_payment = false,
        needs_processing = true,
        number = "ORD12345",
        order_key = "orderkey123",
        parent_id = 0,
        payment_method = paymentMode(selectedPaymentType),
        payment_method_title = "Direct Bank Transfer",
        payment_url = "https://www.plutuscloudserviceuat.in:8201/API/CloudBasedIntegration/V1/",
        prices_include_tax = true,
        refunds = emptyList(),
        shipping = Shipping(
            //TODO: Data Attached
            first_name = mainViewModel.studentViewModel.studentDetails.value?.studentName?.split(" ")?.getOrNull(0) ?: "",
            last_name = mainViewModel.studentViewModel.studentDetails.value?.studentName?.split(" ")?.getOrNull(1) ?: "",
            address_1 = mainViewModel.studentViewModel.studentDetails.value?.billingAddressLine1 ?: "",
            address_2 = mainViewModel.studentViewModel.studentDetails.value?.billingAddressLine2 ?: "",
            city = mainViewModel.studentViewModel.studentDetails.value?.billingCity ?: "",
            state = mainViewModel.studentViewModel.studentDetails.value?.billingState ?: "",
            postcode = mainViewModel.studentViewModel.studentDetails.value?.billingZipCode ?: "",
            phone = mainViewModel.studentViewModel.studentDetails.value?.phoneNumber ?: "",
            country = "India",
            company = "kLoc Technologies",
        ),
        shipping_lines = emptyList(),
        shipping_tax = "0.00",
        shipping_total = "5.00",
        status = "processing",
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
        total = "$totalOrderAmount",
        total_tax = "1.25",
        transaction_id = ptrnNumber,
        version = "1.0"
    )
}

private fun generateTransactionNumber(): String {
    val prefix = "MP"
    val timestamp = System.currentTimeMillis().toString().take(10)
    val uuidPart = UUID.randomUUID().toString().replace("-", "").take(6)
    return "$prefix$timestamp$uuidPart"
}


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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.kloc.unistore.model.viewModel.MainViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kloc.unistore.common.CommonDropdownMenu
import com.kloc.unistore.common.CommonProgressIndicator
import com.kloc.unistore.common.SuccessfulAnimation
import com.kloc.unistore.entity.order.TmcpData
import com.kloc.unistore.entity.pineLabs.billing.AdditionalInfo
import com.kloc.unistore.entity.pineLabs.billing.UploadBilledTransaction
import com.kloc.unistore.entity.pineLabs.status.GetCloudBasedTxnStatus
import com.kloc.unistore.firestoredb.viewmodel.EmployeeViewModel
import com.kloc.unistore.model.paymentViewModel.PaymentViewModel
import com.kloc.unistore.util.Constants
import kotlinx.coroutines.delay
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    productViewModel: ProductViewModel,
    employeeViewModel: EmployeeViewModel,
    orderViewModel: OrderViewModel = hiltViewModel(),
    paymentViewModel: PaymentViewModel = hiltViewModel(),

) {
    var paymentInitiate by remember { mutableStateOf(false) }
    val cartItems by mainViewModel.cartViewModel.cartItems.collectAsState()
    val studentDetails by mainViewModel.studentViewModel.studentDetails.collectAsState()
    val totalAmount = cartItems.sumOf { it.product.price * it.quantity }
    val totalQuantity = cartItems.sumOf { it.quantity }
    var selectedPaymentType by remember { mutableStateOf(0) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)) {
            Text(
                text = "Order Details",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // Products Section
            OrderSectionCard(title = "Products") {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(cartItems) { cartItem ->
                        ProductDetailItem(cartItem)
                        Divider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            OrderSectionCard(title = "Shipping Address") {
                Text(
                    text = studentDetails?.shipingAddress ?: "No shipping address provided",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            OrderSectionCard(title = "Order Summary") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Items",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "$totalQuantity",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Amount",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "₹$totalAmount",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            OrderSectionCard(title = "Payment Details") {
                CommonDropdownMenu(
                    label = "Payment Type",
                    items = (0..46).filter { paymentMode(it) != "Unknown" }.map { paymentMode(it) },
                    selectedItem = paymentMode(selectedPaymentType),
                    onItemSelected = { selectedOption ->
                        selectedPaymentType = (0..46).filter { paymentMode(it) != "Unknown" }.map { paymentMode(it) }.indexOf(selectedOption)
                    }
                )
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
                Text(
                    "Confirm Your Order",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        if (paymentInitiate) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(enabled = false) {}, contentAlignment = Alignment.Center) {
                PaymentProcessingIndicator(
                    mainViewModel,
                    paymentViewModel,
                    productViewModel,
                    orderViewModel,
                    totalAmount,
                    selectedPaymentType,
                    onComplete = { paymentViewModel.resetPaymentData()
                        paymentInitiate = false },
                    onCancel = { paymentViewModel.resetPaymentData()
                        paymentInitiate = false },
                    employeeViewModel
                )
            }
        }
    }
}

@Composable
fun OrderSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}

@Composable
fun ProductDetailItem(cartItem: CartItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = cartItem.product.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                fontSize = 15.sp,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Size: ${cartItem.size}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "x${cartItem.quantity}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "₹${cartItem.product.price * cartItem.quantity}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
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
    onComplete: () -> Unit,
    onCancel: () -> Unit,
    employeeViewModel: EmployeeViewModel
) {
    var isPaymentInitiated by remember { mutableStateOf(false) }
    var createOrder by remember { mutableStateOf(false) }
    var pollingAttempts by remember { mutableStateOf(0) }
    val maxPollingAttempts = 12
    val pollingDelayMillis = 5000L
    val paymentResponse by paymentViewModel.paymentResponse.collectAsState()
    val paymentStatus by paymentViewModel.transactionStatus.collectAsState()
    var orderId by remember { mutableStateOf(0) }
    val transactionNumber by remember { mutableStateOf(generateTransactionNumber()) }
    var remainingTime by remember { mutableStateOf(60) }
    var currentAnimation by remember { mutableStateOf("Payment Processing") }
    // Showing Animations
    when (currentAnimation) {
        "Payment Processing" -> CommonProgressIndicator("", "Processing Payment...\nRemaining Time: ${remainingTime}s", "Cancel Payment") { onCancel() }
        "Payment Successful Creating Order" -> CommonProgressIndicator("", "Payment Successful!\nCreating Order...")
        "Payment Successful Order Created" -> SuccessfulAnimation("Payment Successful!\nOrder Id: $orderId") { onComplete() }
        else -> onCancel()
    }
    // Creating Order And insuring order create once only
    LaunchedEffect(createOrder) {
        if (createOrder) {
            orderViewModel.placeOrder(getOrderDetails(mainViewModel, productViewModel, selectedPaymentType, transactionNumber,employeeViewModel)) { order ->
                orderId = order?.id ?: 0
                createOrder = false
                currentAnimation = if (order != null) "Payment Successful Order Created" else ""
                mainViewModel.showToast(order?.let { "Order Created!" } ?: "Order Creation Failed.")
            }
        }
    }

    // Showing live remaining time for payment
    LaunchedEffect(remainingTime) {
        if (remainingTime > 0) {
            delay(1000L)
            remainingTime -= 1
        } else { onCancel() }
    }
    // Sending request to Pine Lab machine for payment
    LaunchedEffect(isPaymentInitiated) {
        if (!isPaymentInitiated){
            isPaymentInitiated = true
            val paymentRequest = UploadBilledTransaction(
                TransactionNumber = transactionNumber,
                SequenceNumber = Constants.SEQUENCE_NUMBER,
                AllowedPaymentMode = selectedPaymentType.toString(),
                Amount = totalAmount * 100,
                UserID = Constants.USER_ID,
                MerchantID = Constants.MERCHANT_ID,
                SecurityToken = Constants.SECURITY_TOKEN,
                StoreID = Constants.STORE_ID,
                ClientID = Constants.CLIENT_ID,
                AutoCancelDurationInMinutes = 1,
                TotalInvoiceAmount = totalAmount,
                AdditionalInfo = listOf(
                    AdditionalInfo(Tag = "1001", Value = "XYZ"),
                    AdditionalInfo(Tag = "1002", Value = "ABC")
                )
            )
            paymentViewModel.initiatePayment(paymentRequest)
        }
    }
    // Getting payment Response from Pine Lab machine
    LaunchedEffect(paymentResponse) {
        paymentResponse?.let { response ->
            if (response.ResponseCode == 0) {
                pollingAttempts = 0
                while (pollingAttempts < maxPollingAttempts) {
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
                if (paymentViewModel.transactionStatus.value?.ResponseCode == 1001 && pollingAttempts >= maxPollingAttempts) {
                    mainViewModel.showToast("Timed out.")
                    onCancel()
                }
            } else {
                mainViewModel.showToast("Payment failed")
                onCancel()
            }
        }
    }
    // Getting payment status from Pine Lab machine
    LaunchedEffect(paymentStatus) {
        paymentStatus?.let { status ->
            if (status.ResponseCode == 0) {
                currentAnimation = "Payment Successful Creating Order"
                createOrder = true
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
    transactionNumber: String,
    employeeViewModel: EmployeeViewModel
): Order {
    val stampDataMap: MutableMap<String, MutableList<StampData>> = mutableMapOf()
    val staffDetails=employeeViewModel.res1.value.data?.employee
    mainViewModel.cartViewModel.cartItems.value?.forEach { cartItem ->
        val key = cartItem.itemId.toString()
        val stampData = StampData(
            product_id = cartItem.product.id, // Product ID
            quantity = cartItem.quantity, // Quantity
            attributes = Attributes(
                attribute_class = "${mainViewModel.className.toString()}", // Class name
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
                    id = 2,
                    key = "pa_class",
                    value = "${mainViewModel.className.toString()}"
                ),
                OrderMetaData(
                    id = 2,
                    key = "pa_color",
                    value = "${cartItem.color}"
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
            ).filter { it.value != null && it.value.toString().isNotBlank() },
            name = "${mainViewModel.className.toString()}",
            parent_name = mainViewModel.studentViewModel.studentDetails.value?.parentName.orEmpty(),
            price = cartItem.product.price,
            product_id = cartItem.product.id,
            quantity = cartItem.quantity,
            sku = " ",
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
                    value = "${mainViewModel.className.toString()}"
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
            price = mainViewModel.cartViewModel.cartItems.value.sumOf { it.product.price * it.quantity },
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
        is_editable = false,
        line_items = lineItems,
        meta_data = listOf(
            MetaDataX(
                id = 1,
                key = "staff_id",
                value = "${staffDetails?.id} ${staffDetails?.name}"
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
        total = "${mainViewModel.cartViewModel.cartItems.value.sumOf { it.product.price * it.quantity }}",
        total_tax = "1.25",
        transaction_id = transactionNumber,
        version = "1.0"
    )
}

private fun generateTransactionNumber(): String {
    val prefix = "MP"
    val timestamp = System.currentTimeMillis().toString().take(10)
    val uuidPart = UUID.randomUUID().toString().replace("-", "").take(6)
    return "$prefix$timestamp$uuidPart"
}


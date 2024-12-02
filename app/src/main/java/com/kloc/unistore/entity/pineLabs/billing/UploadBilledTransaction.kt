package com.kloc.unistore.entity.pineLabs.billing

data class UploadBilledTransaction(
    val AdditionalInfo: List<AdditionalInfo?>?,
    val AllowedPaymentMode: String?,
    val Amount: Int?,
    val AutoCancelDurationInMinutes: Int?,
    val ClientID: String?,
    val MerchantID: String?,
    val SecurityToken: String?,
    val SequenceNumber: String?,
    val StoreID: String?,
    val TotalInvoiceAmount: Int?,
    val TransactionNumber: String?,
    val UserID: String?
)
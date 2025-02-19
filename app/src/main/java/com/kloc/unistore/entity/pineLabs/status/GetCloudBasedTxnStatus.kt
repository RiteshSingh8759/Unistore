package com.kloc.unistore.entity.pineLabs.status

data class GetCloudBasedTxnStatus(
    val ClientID: String?,
    val MerchantID: String?,
    val PlutusTransactionReferenceID: Int?,
    val SecurityToken: String?,
    val StoreID: String?,
    val UserID: String?,
    val amount: Double
)
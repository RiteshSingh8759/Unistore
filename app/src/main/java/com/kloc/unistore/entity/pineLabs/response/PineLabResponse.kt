package com.kloc.unistore.entity.pineLabs.response

data class PineLabResponse(
    val PlutusTransactionReferenceID: Int?,
    val ResponseCode: Int?,
    val ResponseMessage: String?,
    val TransactionData: List<TransactionData?>?
)
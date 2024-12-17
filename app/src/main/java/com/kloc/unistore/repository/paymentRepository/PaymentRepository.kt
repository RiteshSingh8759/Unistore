package com.kloc.unistore.repository.paymentRepository


import android.util.Log
import com.kloc.unistore.entity.pineLabs.billing.UploadBilledTransaction
import com.kloc.unistore.entity.pineLabs.response.PineLabResponse
import com.kloc.unistore.entity.pineLabs.status.GetCloudBasedTxnStatus
import com.kloc.unistore.service.PineLabsApiService
import javax.inject.Inject

class PaymentRepository @Inject constructor() {
    private val apiService = PineLabsApiService.create()
    // Function to initiate a payment
    suspend fun initiatePayment(request: UploadBilledTransaction): PineLabResponse? {
        return try {
            val response = apiService.initiatePayment(request)
            if (response.isSuccessful) {
                Log.d("debug", "Payment initiation successful: ${response.body()}")
                response.body() // Return the response if successful
            } else {
                Log.e("debug", "Payment initiation failed: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("debug", "Exception during payment initiation", e)
            null
        }
    }
    // Function to get the transaction status
    suspend fun getTransactionStatus(request: GetCloudBasedTxnStatus): PineLabResponse? {
        return try {
            val response = apiService.getTransactionStatus(request)
            if (response.isSuccessful) {
                Log.d("debug", "Transaction status fetch successful: ${response.body()}")
                response.body() // Return the response if successful
            } else {
                Log.e("debug", "Transaction status fetch failed: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("debug", "Exception during fetching transaction status", e)
            null
        }
    }
    // Function to cancel a payment
    suspend fun cancelPayment(request: GetCloudBasedTxnStatus): PineLabResponse? {
        return try {
            val response = apiService.cancelPayment(request)
            if (response.isSuccessful) {
                Log.d("debug", "Payment cancellation successful: ${response.body()}")
                response.body()
            } else {
                Log.e("debug", "Payment cancellation failed: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("debug", "Exception during payment cancellation", e)
            null
        }
    }
}
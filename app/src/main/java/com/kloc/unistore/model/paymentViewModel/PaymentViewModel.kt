package com.kloc.unistore.model.paymentViewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kloc.unistore.entity.pineLabs.billing.UploadBilledTransaction
import com.kloc.unistore.entity.pineLabs.status.GetCloudBasedTxnStatus
import com.kloc.unistore.entity.pineLabs.response.PineLabResponse
import com.kloc.unistore.repository.paymentRepository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val pineLabsRepository: PaymentRepository
) : ViewModel() {
    // StateFlow for payment initiation response
    private val _paymentResponse = MutableStateFlow<PineLabResponse?>(null)
    val paymentResponse: StateFlow<PineLabResponse?> = _paymentResponse
    // StateFlow for transaction status response
    private val _transactionStatus = MutableStateFlow<PineLabResponse?>(null)
    val transactionStatus: StateFlow<PineLabResponse?> = _transactionStatus
    private val _cancelStatus = MutableStateFlow<PineLabResponse?>(null)
    val cancelStatus: StateFlow<PineLabResponse?> = _cancelStatus
    // Function to initiate payment
    fun initiatePayment(request: UploadBilledTransaction) {
        viewModelScope.launch {
            try {
                _paymentResponse.value = null // Reset previous data
                _paymentResponse.value = pineLabsRepository.initiatePayment(request)
            } catch (e: Exception) {
                _paymentResponse.value = null // Handle error by setting null
            }
        }
    }
    // Function to get transaction status
    fun getTransactionStatus(request: GetCloudBasedTxnStatus) {
        viewModelScope.launch {
            try {
                _transactionStatus.value = null // Reset previous data
                _transactionStatus.value = pineLabsRepository.getTransactionStatus(request)
            } catch (e: Exception) {
                _transactionStatus.value = null // Handle error by setting null
            }
        }
    }
    // Function to cancel payment
    fun cancelPayment(request: GetCloudBasedTxnStatus) {
        viewModelScope.launch {
            try {
                val cancelResponse = pineLabsRepository.cancelPayment(request)
                _cancelStatus.value = cancelResponse
                if (cancelResponse?.ResponseCode == 0) {
                    _paymentResponse.value = null
                    _transactionStatus.value = null
                }
            } catch (e: Exception) {
                _cancelStatus.value = null
            }
        }
    }
    // AJ : Reset Payment Data
    fun resetPaymentData() {
        _paymentResponse.value = null
        _transactionStatus.value = null
        _cancelStatus.value = null
    }
}
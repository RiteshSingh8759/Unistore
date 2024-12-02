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
}
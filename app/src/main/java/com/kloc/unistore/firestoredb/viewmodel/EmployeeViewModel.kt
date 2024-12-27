package com.kloc.unistore.firestoredb.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide.init
import com.kloc.unistore.firestoredb.module.AddressModel
import com.kloc.unistore.firestoredb.module.DeviceModel
import com.kloc.unistore.firestoredb.module.EmployeeModel
import com.kloc.unistore.firestoredb.repository.EmployeeRepository
import com.kloc.unistore.util.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class EmployeeViewModel@Inject constructor(
    private val employeeRepository: EmployeeRepository
) : ViewModel()
{

    private val _resList: MutableState<FirestoreState> = mutableStateOf(FirestoreState())
    val res: State<FirestoreState> = _resList
    private val _res: MutableState<singleState> = mutableStateOf(singleState())
    val res1: State<singleState> = _res

    private var _userInput = mutableStateOf("")
    var userInput: State<String> = _userInput

    private val _deviceList:MutableState<FirestoreDeviceState> = mutableStateOf(FirestoreDeviceState())
    val deviceList: State<FirestoreDeviceState> = _deviceList

    private val _schoolAddress:MutableState<FirestoreAddressState> = mutableStateOf(FirestoreAddressState())
    val address: State<FirestoreAddressState> =_schoolAddress

    init {
        getDevice()
    }
    fun updateUserInput(input: String) {
        _userInput.value = input
    }

    fun getUserByEmail(id: String) = viewModelScope.launch {
        employeeRepository.getUserById(id).collect {
            when (it) {
                is ResultState.Success -> {
                    _res.value = singleState(
                        data = it.data
                    )
                }
                is ResultState.Failure -> {
                    _res.value = singleState(
                        error = it.toString()
                    )
                }
                ResultState.Loading -> {
                    _res.value = singleState(
                        isLoading = true
                    )
                }
            }
        }
    }
    fun getDevice() = viewModelScope.launch {
        employeeRepository.getAllDevice().collect {
            when (it) {
                is ResultState.Success -> {
                    _deviceList.value = FirestoreDeviceState(
                        data = it.data
                    )
                }
                is ResultState.Failure -> {
                    _deviceList.value = FirestoreDeviceState(
                        error = it.toString()
                    )
                }
                ResultState.Loading -> {
                    _deviceList.value = FirestoreDeviceState(
                        isLoading = true
                    )
                }
            }
        }
    }
    fun getAddressBySchoolId(school_id: String)= viewModelScope.launch {
        employeeRepository.getAddressById(school_id).collect {
            when (it) {
                is ResultState.Success -> {
                    _schoolAddress.value = FirestoreAddressState(
                        data = it.data
                    )
                }
                is ResultState.Failure -> {
                    _schoolAddress.value = FirestoreAddressState(
                        error = it.toString()
                    )
                }
                ResultState.Loading -> {
                    _schoolAddress.value = FirestoreAddressState(
                        isLoading = true
                    )
                }
            }
        }
    }
}

data class FirestoreState(
    val data:List<EmployeeModel> = emptyList(),
    val error:String = "",
    val isLoading:Boolean = false
)
data class singleState(
    val data: EmployeeModel? = null,
    val error:String = "",
    val isLoading:Boolean = false
)
data class FirestoreDeviceState(
    val data:List<DeviceModel> = emptyList(),
    val error:String = "",
    val isLoading:Boolean = false
)
data class FirestoreAddressState(
    val data:AddressModel? = null,
    val error:String = "",
    val isLoading:Boolean = false
)
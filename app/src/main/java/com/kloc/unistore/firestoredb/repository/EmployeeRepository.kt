package com.kloc.unistore.firestoredb.repository

import com.kloc.unistore.firestoredb.module.AddressModel
import com.kloc.unistore.firestoredb.module.DeviceModel
import com.kloc.unistore.firestoredb.module.EmployeeModel
import com.kloc.unistore.util.ResultState
import kotlinx.coroutines.flow.Flow

interface EmployeeRepository
{

    fun getUserById(id:String) : Flow<ResultState<EmployeeModel>>
    fun getAllDevice():Flow<ResultState<List<DeviceModel>>>
    fun getAddressById(school_id:String) : Flow<ResultState<AddressModel>>
}
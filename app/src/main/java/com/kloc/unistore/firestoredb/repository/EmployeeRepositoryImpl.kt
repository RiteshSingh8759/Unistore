package com.kloc.unistore.firestoredb.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.kloc.unistore.firestoredb.module.AddressModel
import com.kloc.unistore.firestoredb.module.DeviceModel
import com.kloc.unistore.firestoredb.module.EmployeeModel
import com.kloc.unistore.util.ResultState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class EmployeeRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : EmployeeRepository {


    override fun getUserById(id: String): Flow<ResultState<EmployeeModel>> = callbackFlow {
        trySend(ResultState.Loading)

        db.collection("employee")
            .whereEqualTo("id", id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // If no documents are found, return an empty result.
                val users = querySnapshot.map { data ->
                    EmployeeModel(
                        employee = EmployeeModel.FirestoreUser(
                            name = data["name"] as String?,
                            email = data["email"] as String?,
                            id = data["id"] as String
                        ),
                        key = data.id
                    )
                }

                if (users.isNotEmpty()) {
                    trySend(ResultState.Success(users.first())) // Send first user if exists
                } else {
                    trySend(ResultState.Failure(Exception("No user found with this email"))) // Handle empty list case
                }
            }.addOnFailureListener {
                trySend(ResultState.Failure(it)) // Handle failure
            }

        awaitClose {
            close()
        }
    }

    override fun getAllDevice(): Flow<ResultState<List<DeviceModel>>> = callbackFlow {
        trySend(ResultState.Loading)
        db.collection("device")
            .get()
            .addOnSuccessListener {
                val device =  it.map { data->
                    DeviceModel(
                        device = DeviceModel.FirestoreDevice(
                            device_id = data["device_id"] as String?,
                        ),
                        key = data.id
                    )
                }
                trySend(ResultState.Success(device))
            }.addOnFailureListener {
                trySend(ResultState.Failure(it))
            }
        awaitClose {
            close()
        }
    }

    override fun getAddressById(school_id: String): Flow<ResultState<AddressModel>> = callbackFlow {
        trySend(ResultState.Loading)

        db.collection("address")
            .whereEqualTo("school_id", school_id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // If no documents are found, return an empty result.
                val address = querySnapshot.map { data ->
                    AddressModel(
                        address = AddressModel.FirestoreAddress(
                            school_id = data["school_id"] as String?,
                            addressLine1 = data["addressLine1"] as String?,
                            addressLine2 = data["addressLine2"] as String,
                            city = data["city"] as String,
                            state = data["state"] as String,
                            zipcode = data["zipcode"] as String,
                            country = data["country"] as String,
                        ),
                        key = data.id
                    )
                }

                if (address.isNotEmpty()) {
                    trySend(ResultState.Success(address.first())) // Send first user if exists
                } else {
                    trySend(ResultState.Failure(Exception("No address found with this school_id"))) // Handle empty list case
                }
            }.addOnFailureListener {
                trySend(ResultState.Failure(it)) // Handle failure
            }

        awaitClose {
            close()
        }
    }


}
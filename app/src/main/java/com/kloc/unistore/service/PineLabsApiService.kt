package com.kloc.unistore.service


import com.kloc.unistore.entity.pineLabs.billing.UploadBilledTransaction
import com.kloc.unistore.entity.pineLabs.response.PineLabResponse
import com.kloc.unistore.entity.pineLabs.status.GetCloudBasedTxnStatus
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import retrofit2.http.Body
import retrofit2.http.POST

interface PineLabsApiService {

    @POST("CloudBasedIntegration/V1/UploadBilledTransaction")
    suspend fun initiatePayment(
        @Body request: UploadBilledTransaction
    ): Response<PineLabResponse>

    @POST("CloudBasedIntegration/V1/GetCloudBasedTxnStatus")
    suspend fun getTransactionStatus(
        @Body request: GetCloudBasedTxnStatus
    ): Response<PineLabResponse>

    companion object {
        private const val BASE_URL = "https://www.plutuscloudserviceuat.in:8201/API/"

        fun create(): PineLabsApiService {
            val client = OkHttpClient.Builder().build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PineLabsApiService::class.java)
        }
    }
}
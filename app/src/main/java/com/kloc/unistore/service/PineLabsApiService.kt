import android.annotation.SuppressLint
import android.content.Context
import com.kloc.unistore.entity.pineLabs.billing.UploadBilledTransaction
import com.kloc.unistore.entity.pineLabs.response.PineLabResponse
import com.kloc.unistore.entity.pineLabs.status.GetCloudBasedTxnStatus
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.Body
import retrofit2.http.POST
import java.security.cert.X509Certificate
import javax.net.ssl.*
interface PineLabsApiService {
    @POST("CloudBasedIntegration/V1/UploadBilledTransaction")
    suspend fun initiatePayment(
        @Body request: UploadBilledTransaction
    ): Response<PineLabResponse>
    @POST("CloudBasedIntegration/V1/GetCloudBasedTxnStatus")
    suspend fun getTransactionStatus(
        @Body request: GetCloudBasedTxnStatus
    ): Response<PineLabResponse>
    @POST("CloudBasedIntegration/V1/CancelTransactionForced")
    suspend fun cancelPayment(
        @Body request: GetCloudBasedTxnStatus
    ): Response<PineLabResponse>
    companion object {
        private const val BASE_URL = "https://www.plutuscloudservice.in:8201/API/"
        fun create(context: Context): PineLabsApiService {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })
            val sslContext = SSLContext.getInstance("SSL").apply {
                init(null, trustAllCerts, java.security.SecureRandom())
            }
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true }
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PineLabsApiService::class.java)
        }
    }
}

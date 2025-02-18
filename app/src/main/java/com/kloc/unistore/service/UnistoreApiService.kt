package com.kloc.unistore.service

import android.provider.Telephony.Carriers.PASSWORD
import com.google.gson.GsonBuilder
import com.kloc.unistore.entity.order.Order
import com.kloc.unistore.entity.product.DoubleTypeAdapter
import com.kloc.unistore.entity.product.IntTypeAdapter
import com.kloc.unistore.entity.product.MetaData
import com.kloc.unistore.entity.product.MetaDataDeserializer
import com.kloc.unistore.entity.product.Product
import com.kloc.unistore.entity.productCategory.Category
import com.kloc.unistore.entity.productVariation.ProductVariationItem
import com.kloc.unistore.entity.school.SchoolCategory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface UnistoreApiService {

    @GET("products/categories")
    suspend fun fetchSchoolDetailsBySlug(
        @Query("slug") slug: String
    ): Response<List<SchoolCategory>>
    @GET("products/categories")
    suspend fun getCategoriesByParent(
        @Query("parent") parentId: Int): Response<List<Category>>

    @GET("products")
    suspend fun getProductsByCategory(
        @Query("category") categoryId: Int,
        @Query("type") type: String
    ): Response<List<Product>>

    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") productId: Int
    ): Response<Product>

    @POST("orders")
    suspend fun createOrder(
        @Body order: Order
    ): Response<Order>

    @GET("products")
    suspend fun getProductsByIds(
        @Query("include") include: String
    ): Response<List<Product>>
    @GET("products/{id}/variations")
    suspend fun getAllProductVariations(
        @Path("id") productId: Int,
        @Query("per_page") perPage: Int = 100
    ): Response<List<ProductVariationItem>>
    @GET("products/{id}/variations")
    suspend fun getProductVariationsWithSearch(
        @Path("id") productId: Int,
        @Query("search") search: String
    ): Response<List<ProductVariationItem>>
    companion object {
//        Development Server Credentials
        private const val BASE_URL = "https://wordpress-698237-3902553.cloudwaysapps.com/wp-json/wc/v3/"
        private const val USERNAME = "ck_aa0034b814776010e00b55e7b6348b0a457537bd"
        private const val PASSWORD = "cs_9a5e0987cda3c884e9e00ddf0fa25f959c859ecb"

//        Live Server Credentials
//        private const val BASE_URL = "https://unistoreindia.com/wp-json/wc/v3/"
//        private const val USERNAME = "ck_a66c6b54bb458c0e4cbf371e3c79389af9fbb300"
//        private const val PASSWORD = "cs_1dd07ffc9c11cf071c1634ee791228c9d2df491f"

        fun create(): UnistoreApiService {
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .header("Authorization", Credentials.basic(USERNAME, PASSWORD))
                        .build()
                    chain.proceed(request)
                }
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()
            val gson = GsonBuilder()
                .registerTypeAdapter(MetaData::class.java, MetaDataDeserializer())
                .registerTypeAdapter(Int::class.java, IntTypeAdapter())
                .registerTypeAdapter(Double::class.java, DoubleTypeAdapter())
                .create()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(UnistoreApiService::class.java)
        }
    }
}

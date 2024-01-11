package com.hritikbhat.anaesthesiademoapp.utils.network

import com.hritikbhat.anaesthesiademoapp.models.ProductDetails
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("rest/V1/productdetails/{productId}/253620")
    suspend fun getProductDetails(
        @Path("productId") productId: String,
        @Query("lang") lang: String,
        @Query("store") store: String
    ): ProductDetails
}

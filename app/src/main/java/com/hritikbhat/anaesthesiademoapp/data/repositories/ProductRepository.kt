package com.hritikbhat.anaesthesiademoapp.data.repositories

import android.util.Log
import com.hritikbhat.anaesthesiademoapp.utils.network.ApiService
import com.hritikbhat.anaesthesiademoapp.models.OperationResult
import com.hritikbhat.anaesthesiademoapp.models.ProductDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository(private val apiService: ApiService) {
    suspend fun getProductDetails(productId: String, lang: String, store: String): OperationResult<ProductDetails> {

        Log.d("LoginStatus","Completed")

        return withContext(Dispatchers.IO){
                try{
                    val productDetailsResponse = apiService.getProductDetails(productId,lang,store)

                    if (productDetailsResponse != null) {
                        OperationResult.Success(productDetailsResponse) // Resume with true when successful
                    } else {
                        OperationResult.Error("Something Went Wrong") // Resume with false when auth is null
                    }
                }
                catch (e:Exception){
                    OperationResult.Error("Something Went Wrong :: ${e.message}")
                }

                }
    }

}
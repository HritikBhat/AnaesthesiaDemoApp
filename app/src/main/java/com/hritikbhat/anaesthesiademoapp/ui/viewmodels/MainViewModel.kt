package com.hritikbhat.anaesthesiademoapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.hritikbhat.anaesthesiademoapp.utils.network.ApiService
import com.hritikbhat.anaesthesiademoapp.data.repositories.ProductRepository
import com.hritikbhat.anaesthesiademoapp.models.OperationResult
import com.hritikbhat.anaesthesiademoapp.models.ProductDetails
import com.hritikbhat.anaesthesiademoapp.utils.network.RetrofitHelper

class MainViewModel : ViewModel() {

    // Inject your ApiService
    private val apiService: ApiService = RetrofitHelper.getInstance().create(ApiService::class.java)

    private val productRepository = ProductRepository(apiService)


    suspend fun getProductDetails(productId: String, lang: String, store: String): OperationResult<ProductDetails> {
        return productRepository.getProductDetails(productId,lang,store)
    }


}
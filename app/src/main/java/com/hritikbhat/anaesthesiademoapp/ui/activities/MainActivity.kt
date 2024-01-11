package com.hritikbhat.anaesthesiademoapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.hritikbhat.anaesthesiademoapp.R
import com.hritikbhat.anaesthesiademoapp.databinding.ActivityMainBinding
import com.hritikbhat.anaesthesiademoapp.models.Attribute
import com.hritikbhat.anaesthesiademoapp.models.Data
import com.hritikbhat.anaesthesiademoapp.models.OperationResult
import com.hritikbhat.anaesthesiademoapp.models.ProductDetails
import com.hritikbhat.anaesthesiademoapp.ui.viewmodels.MainViewModel
import com.hritikbhat.anaesthesiademoapp.utils.adapters.CarouselAdapter
import com.hritikbhat.anaesthesiademoapp.utils.adapters.ProductColorAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(),ProductColorAdapter.OnItemClickListener {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private val productId = "6701" // Replace this value dynamically as needed
    private val lang = "en"
    private val store = "KWD"
    private var productDetails: ProductDetails ? =null
    private var qty = 1
    private var remainingQty = 1000
    private var isFav = false
    private var isProdInfoExpanded = true


    override fun onSaveInstanceState(outState: Bundle) {
        // Save your state data here to the bundle
        outState.putInt("quantity", qty)
        outState.putBoolean("isFavorites",isFav)
        outState.putBoolean("isProdInfoExpanded",isProdInfoExpanded)
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            qty = savedInstanceState.getInt("quantity")
            isFav = savedInstanceState.getBoolean("isFavorites")
            isProdInfoExpanded = savedInstanceState.getBoolean("isProdInfoExpanded")
        }

        if (!::mainBinding.isInitialized){
            mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        }
        if (!::viewModel.isInitialized){
            viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        }


        val favDrawable = if (isFav) R.drawable.fav_enabled_icon else R.drawable.fav_not_enabled_icon
        mainBinding.favBtn.setImageResource(favDrawable)

        if (isProdInfoExpanded){
            mainBinding.productInfoTV.visibility = View.VISIBLE
            mainBinding.productInfoTrigBtn.setImageResource(R.drawable.ic_arrow_down_24)
        }
        else{
            mainBinding.productInfoTV.visibility = View.GONE
            mainBinding.productInfoTrigBtn.setImageResource(R.drawable.ic_arrow_up_24)
        }

        setSupportActionBar(mainBinding.topAppBar)

        viewModel.viewModelScope.launch {
            async {
                getProductDetails()
            }.join()

            setLayoutWithData()
        }
    }

    private fun setLayoutWithData() {
        if (::mainBinding.isInitialized && productDetails!=null){

            val productDetailData: Data = productDetails?.data as Data
            remainingQty = productDetailData.remaining_qty
            val finalDescString = productDetailData.description

            val floatValue = productDetailData.price.toFloatOrNull() ?: 0.0f

            val loanString = "or 4 interest-free payments"
            val loanString2 = "<b>${"%.2f".format(floatValue / 4)} KWD <u><a href=\"https://tabby.ai/en-KW/business\">Learn More</a></b></u>"


            //Sets Title of the page
            mainBinding.appBarTitle.text = productDetailData.name

            //Sets Carousel And Indicator
            mainBinding.productCarousel.adapter = CarouselAdapter(this,productDetailData.images)
            mainBinding.productCarousel.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            mainBinding.circleIndicator.setViewPager(mainBinding.productCarousel)

            //Sets Product Details
            val priceString = "$floatValue KWD"
            val skuString = "SKU: ${productDetailData.sku}"

            mainBinding.productBrandNameTV.text = productDetailData.brand_name
            mainBinding.productNameTV.text = productDetailData.name
            mainBinding.productSKUTV.text = skuString
            mainBinding.productPriceTV.text = priceString



            //Sets Color Recyclerview
            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            mainBinding.productColorRV.layoutManager = layoutManager
            val colorAdapter = ProductColorAdapter(this,productDetailData.configurable_option[0].attributes)
            colorAdapter.setOnItemClickListener(this)
            mainBinding.productColorRV.adapter = colorAdapter

            //Product Quantity
            mainBinding.addQuantityBtn.setOnClickListener{
                if (qty<remainingQty){
                    mainBinding.quantityET.setText((++qty).toString())
                }}

            mainBinding.reduceQuantityBtn.setOnClickListener{
                if (qty>0){
                    mainBinding.quantityET.setText((--qty).toString())
                } }

            mainBinding.quantityET.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // This method is called before the text is changed

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // This method is called when the text is changing
                    val currQtyStr = s.toString()

                    if (currQtyStr.isNotEmpty()){
                        val currQty = s.toString().toInt()
                        if (currQty in 1..<remainingQty){
                            qty = currQty
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                        viewModel.viewModelScope.launch {
                            withContext(Dispatchers.Main){
                                val qtyStr2 = s.toString()

                                if (qtyStr2.isNotEmpty()) {
                                    if (qtyStr2.toInt()>remainingQty){
                                        qty = remainingQty
                                        mainBinding.quantityET.setText("$qty")
                                        return@withContext
                                    }
                                }

                                delay(1200)
                                val qtyStr = s.toString()

                                if (qtyStr.isEmpty()) {
                                    qty = 0
                                    mainBinding.quantityET.setText("0")
                                }
                            }


                        }
                    // This method is called after the text has changed
                }
            })

            //EMI Layout
            mainBinding.productInfoTV.text = Html.fromHtml(finalDescString,Html.FROM_HTML_MODE_COMPACT ) // for 24 api and more
            mainBinding.productEMITV.text = loanString
            mainBinding.productEMITV2.text = Html.fromHtml(loanString2,Html.FROM_HTML_MODE_COMPACT)
            mainBinding.productEMITV2.movementMethod = LinkMovementMethod.getInstance()


            //Fav Button
            mainBinding.favBtn.setOnClickListener{
                    if (isFav){
                        mainBinding.favBtn.setImageResource(R.drawable.fav_not_enabled_icon)
                    }
                    else{
                        mainBinding.favBtn.setImageResource(R.drawable.fav_enabled_icon)
                        Toast.makeText(this,"Added To Favourites!",Toast.LENGTH_LONG).show()
                    }
                    isFav= !isFav

                }


            //To expand or collapse Product Information
            mainBinding.productInfoTrigBtn.setOnClickListener {
                Log.d("productInfoTrigBtn Click","$isProdInfoExpanded")
                if (isProdInfoExpanded){
                    mainBinding.productInfoTV.visibility = View.GONE
                    mainBinding.productInfoTrigBtn.setImageResource(R.drawable.ic_arrow_up_24)
                }
                else{
                    mainBinding.productInfoTV.visibility = View.VISIBLE
                    mainBinding.productInfoTrigBtn.setImageResource(R.drawable.ic_arrow_down_24)
                }
                isProdInfoExpanded= !isProdInfoExpanded
            }


            //Share Url Functionality
            val commonShareOnClickListener = View.OnClickListener {
                    when (it.id) {
                        R.id.shareBtn, R.id.bottomShareBtn -> {
                            if (productDetails!=null){
                                Log.d("Web URL ZZZ",productDetailData.web_url)
//                                startShareIntent(productDetailData.web_url)
                                val textToShare = productDetailData.web_url

                                val shareIntent = Intent()
                                shareIntent.action = Intent.ACTION_SEND
                                shareIntent.type="text/plain"
                                shareIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(textToShare,Html.FROM_HTML_MODE_COMPACT ).toString())
                                startActivity(Intent.createChooser(shareIntent,getString(R.string.send_to)))
                            }
                        }}}

                mainBinding.shareBtn.setOnClickListener(commonShareOnClickListener)
                mainBinding.bottomShareBtn.setOnClickListener(commonShareOnClickListener)

                mainBinding.cartBtn.setOnClickListener{
                    // Goes to Bag/Cart Section
                }

                mainBinding.bottomAddToBagBtn.setOnClickListener{
                    Toast.makeText(this,"Added To Bag",Toast.LENGTH_LONG).show()
                }

            stopShimmer()
        }
    }

    private fun startShimmer(){
        mainBinding.shimmerLayout.startShimmer()
        mainBinding.mainScrollViewShimmer.visibility = View.VISIBLE
        mainBinding.mainScrollView.visibility = View.GONE
    }

    private fun stopShimmer(){
        mainBinding.shimmerLayout.stopShimmer()
        mainBinding.mainScrollViewShimmer.visibility = View.GONE
        mainBinding.mainScrollView.visibility = View.VISIBLE
    }

    //Retrieve and Process product details from the API.
    private suspend fun getProductDetails() {
        startShimmer()
        productDetails=null
        when (val productDetailsOperationResult: OperationResult<ProductDetails> =
            viewModel.getProductDetails(productId, lang, store)) {
            is OperationResult.Success -> {
                // Operation was successful, handle the result
                try {
                    productDetails = productDetailsOperationResult.data
                    Log.d("Product Details", productDetails?.data.toString())

                } catch (e: Exception) {
                    Toast.makeText(
                        mainBinding.root.context,
                        "Something Went Wrong. Please check your internet connection.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                // Process searchList here
            }

            is OperationResult.Error -> {
                // An error occurred, handle the error
                val errorMessage = productDetailsOperationResult.message
                Log.e("ERROR", errorMessage)
                if (errorMessage.contains("No address associated with hostname")){
                    Toast.makeText(this,"Unable To Connect! Please Check Your Internet Connectivity and Try Again",Toast.LENGTH_LONG).show()
                }
                // Handle the error, for example, display an error message to the user
            }
        }
    }

    override fun onSelectingColorItem(selectedColorAttribute: Attribute) {
        //change price
        val priceString = "${selectedColorAttribute.price} KWD"
        mainBinding.productPriceTV.text = priceString
        //Toast Selected Color Name
        Toast.makeText(this,"Selected Color: ${selectedColorAttribute.value}",Toast.LENGTH_LONG).show()



    }
}
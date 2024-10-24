package com.example.mekato_tessst

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.analytics.ecommerce.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class P_ViewModel(application: Application) : AndroidViewModel(application){
    val dataMutable: MutableLiveData<List<Products>> = MutableLiveData()
    val err: MutableLiveData<String> = MutableLiveData()
    private val _productDetail = MutableLiveData<Products>()
    val productDetail: LiveData<Products> get() = _productDetail


    fun fetchProductDetails(productId: Int) {
        // Call your API or database to get the product details
        ApiClient.getInstance().getProductById(productId).enqueue(object : Callback<Products> {
            override fun onResponse(call: Call<Products>, response: Response<Products>) {
                if (response.isSuccessful) {
                    _productDetail.value = response.body()
                } else {
                    // Handle error
                    Log.e("P_ViewModel", "Error fetching product details")
                }
            }

            override fun onFailure(call: Call<Products>, t: Throwable) {
                // Handle error
                Log.e("P_ViewModel", "Error fetching product details", t)
            }
        })
    }
    // Function to fetch all products
    fun getAllProducts() {
            ApiClient.getInstance().getProducts().enqueue(object : Callback<List<Products>> {
            override fun onResponse(call: Call<List<Products>>, response: Response<List<Products>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        dataMutable.value = it
                    } ?: run {
                        err.value = "No data found"
                    }
                } else {
                    err.value = "Error: ${response.message()}"
                    Log.e("P_ViewModel", "Error fetching products: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Products>>, t: Throwable) {
                err.value = "Error: ${t.message}"
                Log.e("P_ViewModel", "Failed to fetch products: ${t.message}")
            }
        })
    }


    private val _cartItems = MutableLiveData<List<cart_Entity>>(emptyList())
    val cartItems: LiveData<List<cart_Entity>> get() = _cartItems
    private val db: cart_database = cart_database.getinstance(application)
    // Function to fetch cart items from the database
    fun getCartItems() {
        viewModelScope.launch(Dispatchers.IO) {
            val items = db.cart_dao().getAll() // Fetch all items from the database
            Log.d("P_ViewModel", "Cart items fetched: $items") // Log fetched items
            _cartItems.postValue(items) // Update the LiveData using _cartItems
        }
    }

    // Function to add product to cart
    fun addToCart(product: Products) {
        val cartItem = cart_Entity(
            productId = product.id,
            quantity = 1, // Default quantity to 1
            image = product.image,
            title_cart = product.title,
            price_cart = product.price,
            total_cart = product.price // Initial total cart amount
        )

        // Use viewModelScope to launch a coroutine on the IO dispatcher
        viewModelScope.launch(Dispatchers.IO) {
            // Check if the product is already in the cart
            val existingCartItem = db.cart_dao().getProductById(product.id)

            if (existingCartItem == null) {
                // If the product is not in the cart, insert it
                db.cart_dao().insert(cartItem) // Insert in background thread
            } else {
                // If the product is already in the cart, update the quantity and total
                existingCartItem.quantity += 1 // Increment quantity
                existingCartItem.total_cart += product.price // Update total

                // Update the existing item in the cart
                db.cart_dao().update(existingCartItem)
            }

            getCartItems() // Update cart items after insertion or update
        }
    }


}





    // Function to fetch a product by ID
//    fun addProductToCart(product: Products) {
//        fun generateCartId(): Int {
//            return Random().nextInt(1000) // Just a random ID for example
//        }
//
//        fun getUserId(): Int {
//            return 1 // Assuming a static user ID for testing
//        }
//
//        fun getCurrentDate(): String {
//            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//            return sdf.format(Date()) // Current date in String format
//        }
//
//        val currentCartItems = _cartItems.value ?: mutableListOf()
//
//        // Assuming you have only one cart for simplicity
//        var currentCart: Api_Cart? = currentCartItems.firstOrNull()
//
//        // Create a new cart if it doesn't exist
//        if (currentCart == null) {
//            currentCart = Api_Cart(
//                id = generateCartId(),
//                userId = getUserId(),
//                date = getCurrentDate(),
//                products = mutableListOf()
//            )
//            currentCartItems.add(currentCart)
//        }
//
//        // Check if the product already exists in the cart
//        val existingProduct = currentCart.products.find { it.productId == product.id }
//        if (existingProduct != null) {
//            existingProduct.quantity += 1 // Increment quantity
//        } else {
//            currentCart.products.add(Product_cart(productId = product.id, quantity = 1)) // Add new product
//        }
//
//        // Update LiveData
//        _cartItems.value = currentCartItems
//    }



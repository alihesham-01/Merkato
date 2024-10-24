package com.example.mekato_tessst

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val db: cart_database = cart_database.getinstance(application.applicationContext)

    // Private MutableLiveData for cart items and quantities
    private val _cartItems = MutableLiveData<List<cart_Entity>>()
    private val _mutableQuantity = MutableLiveData<Pair<Int, Int>>()

    // Expose LiveData for observation
    val cartItems: LiveData<List<cart_Entity>> get() = _cartItems
    val mutableQuantity: LiveData<Pair<Int, Int>> get() = _mutableQuantity

    // Insert a new cart item
    fun insert(item: cart_Entity) {
        viewModelScope.launch(Dispatchers.IO) {
            db.cart_dao().insert(item)
        }
    }

    // Function to update quantity and save in the database
    fun score_increment_decrement(is_plus: Boolean, cartItem: cart_Entity) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentQuantity = cartItem.quantity
            val newQuantity = if (is_plus) {
                currentQuantity + 1
            } else {
                if (currentQuantity > 1) currentQuantity - 1 else 0 // Prevent negative quantity
            }

            if (newQuantity == 0) {
                db.cart_dao().delete(cartItem)
                Log.d("CartViewModel", "Product deleted: ${cartItem.title_cart}")
                // Refresh the cart items after deletion
                getCartItems()
            } else {
                cartItem.quantity = newQuantity
                cartItem.total_cart = newQuantity * cartItem.price_cart
                db.cart_dao().update(cartItem)
            }

            // Update mutableQuantity LiveData
            _mutableQuantity.postValue(Pair(cartItem.productId, newQuantity))
        }
    }

    // Read cart items (though this method is not really necessary with getCartItems)
    fun read() {
        viewModelScope.launch(Dispatchers.IO) {
            // This method is redundant as getCartItems() already fetches items
            db.cart_dao().getAll()
        }
    }

    // Delete a specific cart item
    fun delete(item: cart_Entity) {
        viewModelScope.launch(Dispatchers.IO) {
            db.cart_dao().delete(item)
            getCartItems() // Refresh the cart items after deletion
        }
    }

    // Delete all items from the cart
    fun deleteAll_viewmoedl() {
        viewModelScope.launch(Dispatchers.IO) {
            db.cart_dao().deleteAll()
            Log.d("CartViewModel", "All cart items deleted from database.")
            getCartItems() // Refresh the cart items after deletion
        }
    }

    // Function to get the total sum of items (implementation needed in DAO)
    fun getSum() {
        viewModelScope.launch(Dispatchers.IO) {
            db.cart_dao().getSum()
        }
    }

    // Get a specific product by ID (implementation needed in DAO)
    fun getProductById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            db.cart_dao().getProductById(id)
        }
    }

    // Update an existing cart item
    fun update(item: cart_Entity) {
        viewModelScope.launch(Dispatchers.IO) {
            db.cart_dao().update(item)
        }
    }

    // Fetch all cart items
    fun getCartItems() {
        viewModelScope.launch(Dispatchers.IO) {
            val items = db.cart_dao().getAll() // Fetch all items from the database
            Log.d("P_ViewModel", "Cart items fetched: $items") // Log fetched items
            _cartItems.postValue(items) // Update the LiveData
        }
    }
}

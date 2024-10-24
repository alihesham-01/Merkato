package com.example.mekato_tessst

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mekato_tessst.databinding.CartDesignBinding
import kotlin.math.roundToInt

class Cart_adapter(var cartList: List<cart_Entity>, var viewModel: CartViewModel) : RecyclerView.Adapter<Cart_adapter.CartHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateCartItems(newCartItems: List<cart_Entity>) {
        this.cartList = newCartItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartHolder {
        val binding = CartDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartHolder(binding)
    }

    override fun onBindViewHolder(holder: CartHolder, position: Int) {
        val cartItem = cartList[position]
        holder.bindCart(cartItem, viewModel)
    }

    override fun getItemCount(): Int = cartList.size

    class CartHolder(private val binding: CartDesignBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bindCart(cart: cart_Entity, viewModel: CartViewModel) {
            // Load product image using Glide
            Glide.with(binding.root.context)
                .load(cart.image)
                .into(binding.cartProductImage)

            // Set product details
            binding.cartProductName.text = cart.title_cart
            binding.cartProductPrice.text = "${cart.price_cart} $"
            binding.productQuantityValue.text = "Quantity: ${cart.quantity}"
            binding.tvTotalPrice.text = "${(cart.price_cart * cart.quantity).roundToInt()} $"

            // Minus button click listener
            binding.decreaseQuantityButton.setOnClickListener {
                viewModel.score_increment_decrement(false, cart)
            }

            // Plus button click listener
            binding.increaseQuantityButton.setOnClickListener {
                viewModel.score_increment_decrement(true, cart)
            }

            // Remove from cart button click listener
            binding.removeFromCartButton.setOnClickListener {
                viewModel.delete(cart)
                viewModel.getCartItems() // Refresh cart items after deletion
            }

            // Observe LiveData for quantity updates
            viewModel.mutableQuantity.observeForever { newQuantity ->
                if (cart.productId == newQuantity.first) { // Check if productId matches
                    binding.productQuantityValue.text = "Quantity: ${newQuantity.second}"
                    binding.tvTotalPrice.text = "Total: ${(newQuantity.second * cart.price_cart).roundToInt()} $" // Update total price
                }
            }
        }
    }
}

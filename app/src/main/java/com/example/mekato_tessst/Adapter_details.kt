package com.example.mekato_tessst

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mekato_tessst.Products
import com.example.mekato_tessst.databinding.ProductDetailsCardviewBinding

class Adapter_details : RecyclerView.Adapter<HolderDetails>() {
    private var productList: List<Products> = listOf()
    private var onAddToCartClickListener: ((Products) -> Unit)? = null // Function type to handle add to cart

    @SuppressLint("NotifyDataSetChanged")
    fun setProductList(products: List<Products>) {
        productList = products
        notifyDataSetChanged()
    }

    fun setOnAddToCartClickListener(listener: (Products) -> Unit) {
        onAddToCartClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDetails {
        val binding: ProductDetailsCardviewBinding = ProductDetailsCardviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HolderDetails(binding)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: HolderDetails, position: Int) {
        val item = productList[position]
        holder.bind(item, onAddToCartClickListener) // Pass listener to bind method
    }
}

class HolderDetails(val binding: ProductDetailsCardviewBinding) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(data: Products, onAddToCartClickListener: ((Products) -> Unit)?) {
        Glide.with(binding.root.context)
            .load(data.image)
            .into(binding.productImage)

        binding.productCategory.text = data.category
        binding.productDescription.text = data.description
        binding.productPrice.text = "Price: ${data.price} EGP"
        binding.productTitle.text = data.title

        binding.addToCartButton.setOnClickListener {
            onAddToCartClickListener?.invoke(data) // Invoke add to cart listener
        }
    }
}
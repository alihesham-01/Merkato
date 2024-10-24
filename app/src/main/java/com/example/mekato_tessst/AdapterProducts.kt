package com.example.mekato_tessst

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mekato_tessst.databinding.CategoriesviewBinding


class AdapterProducts(private val onItemClick: (Products) -> Unit) : RecyclerView.Adapter<Holder>() {

    private var productList: List<Products> = listOf()

    @SuppressLint("NotifyDataSetChanged")
    fun updateProductList(items: List<Products>) {
        this.productList = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding: CategoriesviewBinding = CategoriesviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return Holder(binding, onItemClick)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = productList[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }
}

class Holder(val binding: CategoriesviewBinding, private val onItemClick: (Products) -> Unit) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(data: Products) {
        Glide.with(binding.root.context)
            .load(data.image)
            .into(binding.productImage)

        binding.productTitle.text = data.title
        binding.productPrice.text = "Price: ${data.price} EGP"
        binding.productRating.text = data.rating.toString()

        // Set click listener for item view
        binding.productsDesign.setOnClickListener {
            onItemClick(data)
        }
    }
}
package com.example.mekato_tessst

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mekato_tessst.databinding.FragmentProductsBinding

class ProductsFragment : Fragment() {

    private lateinit var binding: FragmentProductsBinding
    private lateinit var viewModel: P_ViewModel
    private lateinit var productAdapter: AdapterProducts // Adapter for the product list
    private lateinit var productDetailsAdapter: Adapter_details // Adapter for product details
    private var isCardVisible = false // Track the visibility state of the card view

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductsBinding.inflate(inflater, container, false)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { findNavController().popBackStack() }

        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(P_ViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Set up the RecyclerView for products
        productAdapter = AdapterProducts { product: Products ->
            viewModel.fetchProductDetails(product.id)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = productAdapter

        // Set up the RecyclerView for product details
        productDetailsAdapter = Adapter_details()
        binding.productDetailsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.productDetailsRecycler.adapter = productDetailsAdapter

        viewModel.getAllProducts()

        // Observe product list for hot deals
        viewModel.dataMutable.observe(viewLifecycleOwner) { products ->
            products?.let {
                productAdapter.updateProductList(it) // Update the list
            }
        }

        // Observe product detail
        viewModel.productDetail.observe(viewLifecycleOwner) { product ->
            product?.let {
                val detailsList = listOf(it)
                productDetailsAdapter.setProductList(detailsList)

                // Set up the click listener for the "Add to Cart" button
                productDetailsAdapter.setOnAddToCartClickListener { product ->
                    viewModel.addToCart(product) // Using the new addToCart method
                    Toast.makeText(requireContext(), "${product.title} added to cart", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Set click listener to toggle card view
        binding.cardviewNav.setOnClickListener { toggleCardView() } // Replace with your actual button ID

        // Handle BottomNavigationView interactions
        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.selectedItemId = R.id.menu_products
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_products -> true
                R.id.menu_cart -> {
                    findNavController().navigate(R.id.action_productsFragment_to_cartFragment)
                    true
                }
                R.id.menu_user -> {
                    findNavController().navigate(R.id.action_productsFragment_to_userFragment)
                    true
                }
                else -> false
            }
        }
        return binding.root
    }

    // Toggle card view visibility with animation
    private fun toggleCardView() {
        if (isCardVisible) {
            // Slide down and hide the card view
            val slideDown = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down)
            binding.cardviewNav.startAnimation(slideDown)
            binding.cardviewNav.visibility = View.GONE
        } else {
            // Slide up and show the card view
            val slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
            binding.cardviewNav.startAnimation(slideUp)
            binding.cardviewNav.visibility = View.VISIBLE
        }

        // Toggle the card visibility state
        isCardVisible = !isCardVisible
    }
}

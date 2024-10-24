package com.example.mekato_tessst

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mekato_tessst.databinding.FragmentCartBinding

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var viewModel: P_ViewModel
    private lateinit var cartViewModel: CartViewModel
    private lateinit var cartAdapter: Cart_adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { findNavController().popBackStack() }

        // Initialize ViewModels
        viewModel = ViewModelProvider(this)[P_ViewModel::class.java]
        cartViewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)


        // Set up RecyclerView
        cartAdapter = Cart_adapter(emptyList(), cartViewModel) // Pass empty list and cartViewModel
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.cartRecyclerView.adapter = cartAdapter

        // Observe cart items
        viewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            cartItems?.let {
                cartAdapter.updateCartItems(it)
            }
        }
        binding.tvClearCart.setOnClickListener {
            cartViewModel.deleteAll_viewmoedl() // Clear all items from the cart

            // Trigger an update to the cart items
            viewModel.getCartItems() // Fetch updated cart items
        }

        viewModel.getCartItems()

        // Handle BottomNavigationView interactions
        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.selectedItemId = R.id.menu_cart
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_products -> {
                    findNavController().navigate(R.id.action_cartFragment_to_productsFragment)
                    true
                }
                R.id.menu_cart -> {
                    // Navigate to Cart Fragment

                    true
                }
                R.id.menu_user -> {
                    // Navigate to User Fragment
                    findNavController().navigate(R.id.action_cartFragment_to_userFragment)
                    true
                }
                else -> false
            }
        }
        return binding.root
    }
}


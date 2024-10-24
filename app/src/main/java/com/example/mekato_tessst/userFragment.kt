package com.example.mekato_tessst

import android.annotation.SuppressLint
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mekato_tessst.databinding.FragmentUserBinding

class UserFragment : Fragment() {
    private lateinit var binding: FragmentUserBinding
    private lateinit var userDB: UserDB
    private val userViewModel: UserViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            UserViewModelFactory(userDB.userDao())
        )[UserViewModel::class.java]
    }
    private var selectedImageUri: Uri? = null
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false)
        userDB = UserDB.getDatabase(requireContext())

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        // Observe user details LiveData
        userViewModel.userDetails.observe(viewLifecycleOwner) { users ->
            if (users != null) {
                displayUserInfo(users)
            } else {
                displayEmptyState()
            }
        }

        // Handle "Edit Account" button click
        binding.editAccountButton.setOnClickListener {
            showEditDialog()
        }

        // BottomNavigationView interactions
        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.selectedItemId = R.id.menu_user
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_products -> {
                    findNavController().navigate(R.id.action_userFragment_to_productsFragment)
                    true
                }
                R.id.menu_cart -> {
                    findNavController().navigate(R.id.action_userFragment_to_cartFragment)
                    true
                }
                R.id.menu_user -> true
                else -> false
            }
        }

        return binding.root
    }

    private fun showEditDialog() {
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_edit_user)

        val editFirstName = dialog.findViewById<EditText>(R.id.edit_first_name)
        val editLastName = dialog.findViewById<EditText>(R.id.edit_last_name)
        val editEmail = dialog.findViewById<EditText>(R.id.edit_email)
        val saveButton = dialog.findViewById<Button>(R.id.save_button)

        // Set initial values from user details
        editFirstName.setText(binding.userFirstname.text.toString().replace("First Name: ", ""))
        editLastName.setText(binding.userLastname.text.toString().replace("Last Name: ", ""))
        editEmail.setText(binding.userEmail.text.toString().replace("Email: ", ""))

        // Handle save button click
        saveButton.setOnClickListener {
            val updatedFirstName = editFirstName.text.toString()
            val updatedLastName = editLastName.text.toString()
            val updatedEmail = editEmail.text.toString()

            if (updatedFirstName.isNotEmpty() && updatedLastName.isNotEmpty() && updatedEmail.isNotEmpty()) {
                updateUserDetails(updatedFirstName, updatedLastName, updatedEmail)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun displayUserInfo(user: UserEntity) {
        binding.userFirstname.text = "First Name: ${user.firstname}"
        binding.userLastname.text = "Last Name: ${user.lastname}"
        binding.userEmail.text = "Email: ${user.email}"

        Glide.with(this@UserFragment)
            .load(user.profileImageUrl)
            .into(binding.profileImage)
    }

    @SuppressLint("SetTextI18n")
    private fun displayEmptyState() {
        binding.userFirstname.text = "First Name: Not Available"
        binding.userLastname.text = "Last Name: Not Available"
        binding.userEmail.text = "Email: Not Available"
    }

    @SuppressLint("SetTextI18n")
    private fun updateUserDetails(firstName: String, lastName: String, email: String) {
        // Update the displayed user information
        binding.userFirstname.text = "First Name: $firstName"
        binding.userLastname.text = "Last Name: $lastName"
        binding.userEmail.text = "Email: $email"

        // You can also update these details in your ViewModel/Database here
        Toast.makeText(context, "User details updated", Toast.LENGTH_SHORT).show()
    }
}


package com.example.greatchem.presentation.profile

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.greatchem.databinding.FragmentAccountBinding
import com.example.greatchem.presentation.auth.AuthActivity
import com.example.greatchem.utils.ToastUtils
import com.example.greatchem.utils.ToastUtils.CustomToastType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private var originalFullName: String? = null
    private var originalPhoneNumber: String? = null
    private var originalAddress: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val toolbar: Toolbar = binding.toolbar
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        loadUserData()
        addTextWatchers()

        binding.editProfileButton.setOnClickListener {
            saveChanges()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // Clear any previous error when text changes
                binding.textInputLayoutFullName.error = null
                binding.textInputLayoutPhoneNumber.error = null
                binding.textInputLayoutAddress.error = null
                checkChanges()
            }
        }
        binding.editTextFullName.addTextChangedListener(textWatcher)
        binding.editTextPhoneNumber.addTextChangedListener(textWatcher)
        binding.editTextAddress.addTextChangedListener(textWatcher)
    }

    private fun checkChanges() {
        val currentFullName = binding.editTextFullName.text.toString()
        val currentPhoneNumber = binding.editTextPhoneNumber.text.toString()
        val currentAddress = binding.editTextAddress.text.toString()

        val hasChanges = (currentFullName != originalFullName) ||
                (currentPhoneNumber != originalPhoneNumber) ||
                (currentAddress != originalAddress)

        binding.editProfileButton.isEnabled = hasChanges
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Anda tidak login.", Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
            return
        }

        val userDocRef = firestore.collection("users").document(currentUser.uid)

        userDocRef.get()
            .addOnSuccessListener { document ->
                val name = document.getString("name") ?: ""
                val phone = document.getString("phone") ?: ""
                val address = document.getString("address") ?: ""

                binding.editTextFullName.setText(name)
                binding.editTextPhoneNumber.setText(phone)
                binding.editTextAddress.setText(address)

                originalFullName = name
                originalPhoneNumber = phone
                originalAddress = address

                checkChanges()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Gagal memuat data: ${exception.message}", Toast.LENGTH_LONG).show()
                binding.editTextFullName.setText("")
                binding.editTextPhoneNumber.setText("")
                binding.editTextAddress.setText("")

                originalFullName = ""
                originalPhoneNumber = ""
                originalAddress = ""
                checkChanges()
            }
    }

    private fun saveChanges() {
        val newFullName = binding.editTextFullName.text.toString().trim()
        val newPhoneNumber = binding.editTextPhoneNumber.text.toString().trim()
        val newAddress = binding.editTextAddress.text.toString().trim()

        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(requireContext(), "Anda tidak login.", Toast.LENGTH_SHORT).show()
            return
        }

        // --- MARKED: VALIDASI INPUT BARU ---
        var isValid = true
        if (newFullName.isEmpty()) {
            binding.textInputLayoutFullName.error = "Nama Lengkap wajib diisi"
            isValid = false
        } else {
            binding.textInputLayoutFullName.error = null // Clear error
        }

        if (newPhoneNumber.isEmpty()) {
            binding.textInputLayoutPhoneNumber.error = "Nomor Telepon wajib diisi"
            isValid = false
        } else {
            binding.textInputLayoutPhoneNumber.error = null // Clear error
        }

        if (newAddress.isEmpty()) {
            binding.textInputLayoutAddress.error = "Alamat wajib diisi"
            isValid = false
        } else {
            binding.textInputLayoutAddress.error = null // Clear error
        }

        if (!isValid) {
            Toast.makeText(requireContext(), "Harap isi semua kolom yang wajib.", Toast.LENGTH_LONG).show()
            return // Stop the function if validation fails
        }
        // --- MARKED: AKHIR VALIDASI INPUT BARU ---

        CoroutineScope(Dispatchers.Main).launch {
            var allUpdatesSuccessful = true
            var errorMessage: String? = null

            val firestoreUpdates = mutableMapOf<String, Any>()

            // Update Display Name in Firebase Auth and Firestore
            firestoreUpdates["name"] = newFullName // Always update Firestore 'name' field
            if (newFullName != originalFullName) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(newFullName)
                    .build()
                try {
                    currentUser.updateProfile(profileUpdates).await()
                } catch (e: Exception) {
                    allUpdatesSuccessful = false
                    errorMessage = "Gagal memperbarui nama profil: ${e.message}"
                }
            }

            if (!allUpdatesSuccessful && errorMessage != null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                }
                return@launch
            }

            // Update Phone Number in Firestore
            firestoreUpdates["phone"] = newPhoneNumber
            // No Auth operation for phone number

            // Update Address in Firestore
            firestoreUpdates["address"] = newAddress
            // No Auth operation for address

            // Perform Firestore update if there are any changes (or if we already added to firestoreUpdates)
            if (firestoreUpdates.isNotEmpty()) {
                try {
                    firestore.collection("users").document(currentUser.uid)
                        .update(firestoreUpdates).await()

                    // Update original values only if Firestore update is successful
                    originalFullName = newFullName
                    originalPhoneNumber = newPhoneNumber
                    originalAddress = newAddress

                } catch (e: Exception) {
                    allUpdatesSuccessful = false
                    errorMessage = "Gagal menyimpan perubahan data: ${e.message}"
                }
            } else {
                // This case should ideally not be reached if inputs are always added to firestoreUpdates
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Tidak ada perubahan untuk disimpan.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            // Provide final feedback based on all operations
            withContext(Dispatchers.Main) {
                if (allUpdatesSuccessful) {
                    ToastUtils.showCustomToast(requireContext(), "Berhasil!", "Semua perubahan berhasil disimpan!", CustomToastType.SUCCESS, Toast.LENGTH_SHORT)
                    checkChanges() // Disable the button
                } else {
                    Toast.makeText(requireContext(), errorMessage ?: "Terjadi kesalahan yang tidak diketahui saat menyimpan.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
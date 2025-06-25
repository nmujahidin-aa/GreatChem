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
import com.example.greatchem.databinding.FragmentChangePasswordBinding
import com.example.greatchem.presentation.auth.AuthActivity
import com.example.greatchem.utils.ToastUtils
import com.example.greatchem.utils.ToastUtils.CustomToastType
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ChangePasswordFragment : Fragment() {
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private var originalEmail: String? = null // To store the email loaded from Firebase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val toolbar: Toolbar = binding.toolbar
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        loadUserData() // Load current email
        addTextWatchers() // Set up listeners for input changes

        binding.editProfileButton.setOnClickListener {
            saveChanges() // Handle save button click
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
                binding.textInputLayoutOldPassword.error = null
                binding.textInputLayoutNewPassword.error = null
                binding.textInputLayoutConfirmNewPassword.error = null
                checkChanges() // Re-evaluate button state
            }
        }
        binding.editTextOldPassword.addTextChangedListener(textWatcher)
        binding.editTextNewPassword.addTextChangedListener(textWatcher)
        binding.editTextConfirmNewPassword.addTextChangedListener(textWatcher)
    }

    private fun checkChanges() {
        val oldPassword = binding.editTextOldPassword.text.toString().trim()
        val newPassword = binding.editTextNewPassword.text.toString().trim()
        val confirmNewPassword = binding.editTextConfirmNewPassword.text.toString().trim()

        // Check if any password fields have content (indicating an intent to change password)
        val hasPasswordInput = oldPassword.isNotEmpty() || newPassword.isNotEmpty() || confirmNewPassword.isNotEmpty()

        // The button is enabled if either email has changed OR password fields have content
        binding.editProfileButton.isEnabled = hasPasswordInput
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

        // Load the current email and display it
        originalEmail = currentUser.email
        binding.editTextEmail.setText(originalEmail)
        checkChanges() // Initial check for button state
    }

    private fun saveChanges() {
        val oldPassword = binding.editTextOldPassword.text.toString().trim()
        val newPassword = binding.editTextNewPassword.text.toString().trim()
        val confirmNewPassword = binding.editTextConfirmNewPassword.text.toString().trim()

        val currentUser = auth.currentUser

        if (currentUser == null) {
            ToastUtils.showCustomToast(requireContext(), "Kesalahan", "Anda tidak login. Silakan login kembali.", CustomToastType.ERROR, Toast.LENGTH_LONG)
            return
        }

        // Determine which changes are intended
        val isChangingPassword = newPassword.isNotEmpty() || confirmNewPassword.isNotEmpty()

        // If nothing is intended to change, show info and return
        if (!isChangingPassword) {
            ToastUtils.showCustomToast(requireContext(), "Informasi", "Tidak ada perubahan untuk disimpan.", CustomToastType.INFO, Toast.LENGTH_SHORT)
            return
        }

        // --- Validation ---
        var isValid = true

        // Password validation if password is being changed
        if (isChangingPassword) {
            if (oldPassword.isEmpty()) {
                binding.textInputLayoutOldPassword.error = "Kata Sandi Lama wajib diisi"
                isValid = false
            } else {
                binding.textInputLayoutOldPassword.error = null
            }

            if (newPassword.isEmpty()) {
                binding.textInputLayoutNewPassword.error = "Kata Sandi Baru wajib diisi"
                isValid = false
            } else if (newPassword.length < 6) {
                binding.textInputLayoutNewPassword.error = "Kata Sandi Baru minimal 6 karakter"
                isValid = false
            } else {
                binding.textInputLayoutNewPassword.error = null
            }

            if (confirmNewPassword.isEmpty()) {
                binding.textInputLayoutConfirmNewPassword.error = "Konfirmasi Kata Sandi Baru wajib diisi"
                isValid = false
            } else if (newPassword != confirmNewPassword) {
                binding.textInputLayoutConfirmNewPassword.error = "Kata Sandi Baru dan Konfirmasi tidak cocok"
                isValid = false
            } else {
                binding.textInputLayoutConfirmNewPassword.error = null
            }
        }

        // **Crucial Check:** If any change is attempted (email or password), old password is REQUIRED
        if ((isChangingPassword) && oldPassword.isEmpty()) {
            binding.textInputLayoutOldPassword.error = "Kata Sandi Lama wajib diisi untuk melakukan perubahan."
            isValid = false
        }


        if (!isValid) {
            ToastUtils.showCustomToast(requireContext(), "Kesalahan", "Harap perbaiki kesalahan di formulir.", CustomToastType.ERROR, Toast.LENGTH_LONG)
            return // Stop if validation fails
        }

        // --- Re-authenticate the user ---
        // This is necessary for any sensitive operations like changing email or password
        val credential = EmailAuthProvider.getCredential(currentUser.email ?: "", oldPassword)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                // 1. Re-authenticate the user
                withContext(Dispatchers.IO) {
                    currentUser.reauthenticate(credential).await()
                }

                // If re-authentication is successful, proceed with updates
                var successfulUpdates = 0
                var anyUpdateFailed = false


                // 2. Update Password (if intended)
                if (isChangingPassword) {
                    try {
                        withContext(Dispatchers.IO) {
                            currentUser.updatePassword(newPassword).await()
                        }
                        successfulUpdates++
                        // Clear password fields on success for security
                        binding.editTextOldPassword.setText("")
                        binding.editTextNewPassword.setText("")
                        binding.editTextConfirmNewPassword.setText("")
                    } catch (e: Exception) {
                        anyUpdateFailed = true
                        ToastUtils.showCustomToast(requireContext(), "Kesalahan", "Gagal memperbarui kata sandi: ${e.message}", CustomToastType.ERROR, Toast.LENGTH_LONG)
                    }
                }

                withContext(Dispatchers.Main) {
                    if (successfulUpdates > 0 && !anyUpdateFailed) {
                        ToastUtils.showCustomToast(requireContext(), "Berhasil!", "Perubahan berhasil disimpan!", CustomToastType.SUCCESS, Toast.LENGTH_SHORT)
                        checkChanges() // Disable button if no more changes
                    } else if (successfulUpdates > 0 && anyUpdateFailed) {
                        ToastUtils.showCustomToast(requireContext(), "Peringatan", "Beberapa perubahan berhasil disimpan, tetapi ada kesalahan pada yang lain.", CustomToastType.WARNING, Toast.LENGTH_LONG)
                    } else {
                        ToastUtils.showCustomToast(requireContext(), "Gagal", "Tidak ada perubahan yang berhasil disimpan.", CustomToastType.ERROR, Toast.LENGTH_LONG)
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Handle re-authentication failure
                    if (e.message?.contains("CREDENTIAL_TOO_OLD_LOGIN_AGAIN") == true || e.message?.contains("requires recent login") == true) {
                        ToastUtils.showCustomToast(requireContext(), "Kesalahan", "Sesi Anda telah habis. Mohon login kembali untuk memperbarui informasi sensitif Anda.", CustomToastType.ERROR, Toast.LENGTH_LONG)
                        // Redirect to login
                        val intent = Intent(activity, AuthActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        requireActivity().finish()
                    } else if (e.message?.contains("WRONG_PASSWORD") == true || e.message?.contains("wrong password") == true) {
                        binding.textInputLayoutOldPassword.error = "Kata sandi lama salah."
                        ToastUtils.showCustomToast(requireContext(), "Kesalahan", "Kata sandi lama Anda salah. Mohon coba lagi.", CustomToastType.ERROR, Toast.LENGTH_LONG)
                    } else {
                        ToastUtils.showCustomToast(requireContext(), "Kesalahan", "Terjadi kesalahan saat otentikasi: ${e.message}", CustomToastType.ERROR, Toast.LENGTH_LONG)
                    }
                }
            }
        }
    }
}
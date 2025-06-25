package com.example.greatchem.presentation.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.example.greatchem.App
import com.example.greatchem.R
import com.example.greatchem.utils.ToastUtils
import com.example.greatchem.utils.ToastUtils.CustomToastType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var backToLoginTextView: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = (requireActivity().application as App).firebaseAuth
        firestore = FirebaseFirestore.getInstance()

        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        registerButton = view.findViewById(R.id.registerButton)
        backToLoginTextView = view.findViewById(R.id.backToLoginTextView)

        registerButton.setOnClickListener {
            performRegistration()
        }

        backToLoginTextView.setOnClickListener {
            // Beri tahu AuthActivity untuk kembali ke LoginFragment
            setFragmentResult("register_navigation_request", bundleOf("destination" to "back_to_login"))
        }
    }

    private fun performRegistration() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Email dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        // Tambahkan validasi password minimal jika belum ada di Firebase Auth
        if (password.length < 6) { // Firebase default minimum 6 karakter
            Toast.makeText(context, "Password minimal 6 karakter.", Toast.LENGTH_SHORT).show()
            return
        }


        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d("RegisterFragment", "createUserWithEmail:success")
                    val user = firebaseAuth.currentUser
                    user?.let { currentUser -> // Mengganti 'it' dengan 'currentUser' untuk kejelasan
                        // Data awal yang ingin disimpan di Firestore
                        val userData = hashMapOf(
                            "name" to (currentUser.displayName ?: email), // Gunakan email sebagai default jika display name null
                            "email" to currentUser.email,
                            "phone" to null // Default null, pengguna bisa mengisinya nanti di profil
                        )

                        // Gunakan currentUser.uid di sini
                        firestore.collection("users").document(currentUser.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                Log.d("RegisterFragment", "User data saved to Firestore successfully for UID: ${currentUser.uid}") // Perbaikan di sini
                                ToastUtils.showCustomToast(requireContext(), "Berhasil!", "Silahkan masuk", CustomToastType.SUCCESS, Toast.LENGTH_SHORT)
                                // Beri tahu AuthActivity setelah data Firestore berhasil disimpan
                                setFragmentResult("register_navigation_request", bundleOf("destination" to "auth_success"))
                            }
                            .addOnFailureListener { e ->
                                Log.e("RegisterFragment", "Error saving user data to Firestore", e)
                                ToastUtils.showCustomToast(requireContext(), "Gagal!", "Pendaftaran berhasil, tetapi gagal menyimpan data tambahan: ${e.message}", CustomToastType.ERROR, Toast.LENGTH_LONG)
                                setFragmentResult("register_navigation_request", bundleOf("destination" to "auth_success"))
                            }
                    } ?: run {
                        Log.e("RegisterFragment", "User is null after successful registration.")
                        ToastUtils.showCustomToast(requireContext(), "Error!", "Pendaftaran berhasil, namun terjadi masalah pada data pengguna.", CustomToastType.ERROR, Toast.LENGTH_LONG)
                        setFragmentResult("register_navigation_request", bundleOf("destination" to "auth_success"))
                    }
                } else {
                    Log.w("RegisterFragment", "createUserWithEmail:failure", task.exception)
                    when (task.exception) {
                        is FirebaseAuthWeakPasswordException -> Toast.makeText(context, "Password terlalu lemah.", Toast.LENGTH_SHORT).show()
                        is FirebaseAuthInvalidCredentialsException -> Toast.makeText(context, "Email tidak valid atau sudah digunakan.", Toast.LENGTH_SHORT).show()
                        is FirebaseAuthUserCollisionException -> Toast.makeText(context, "Akun dengan email ini sudah ada.", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(context, "Pendaftaran gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}
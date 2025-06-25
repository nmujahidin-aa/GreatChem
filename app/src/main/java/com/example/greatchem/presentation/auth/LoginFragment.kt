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
import com.google.firebase.auth.FirebaseAuth
import com.example.greatchem.App
import com.example.greatchem.R
import com.example.greatchem.utils.ToastUtils
import com.example.greatchem.utils.ToastUtils.CustomToastType
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class LoginFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerTextView: TextView
    private lateinit var forgotPasswordTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil instance FirebaseAuth dari Activity (melalui App class)
        firebaseAuth = (requireActivity().application as App).firebaseAuth

        // Inisialisasi Views
//        emailEditText = view.findViewById(R.id.emailEditText)
//        passwordEditText = view.findViewById(R.id.passwordEditText)
//        loginButton = view.findViewById(R.id.loginButton)
//        registerTextView = view.findViewById(R.id.signUpLink)
//        forgotPasswordTextView = view.findViewById(R.id.forgotPasswordTextView)

        emailEditText = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.emailEditText)
        passwordEditText = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.passwordEditText)
        loginButton = view.findViewById<android.widget.Button>(R.id.loginButton) // Atau <com.google.android.material.button.MaterialButton>
        registerTextView = view.findViewById<android.widget.TextView>(R.id.signUpLink)
        forgotPasswordTextView = view.findViewById<android.widget.TextView>(R.id.forgotPasswordTextView)

        // Jika Anda menggunakan MaterialButton untuk loginButton, lebih baik secara eksplisit menggunakan:
        // val loginButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.loginButton)

        // Anda juga mungkin ingin mengakses TextInputLayouts untuk mengambil teks atau mengatur error
        val emailInputLayout = view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.emailTextInputLayout)
        val passwordInputLayout = view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.passwordTextInputLayout)

        // Untuk checkbox "Remember me next time"
        val rememberMeCheckBox = view.findViewById<android.widget.CheckBox>(R.id.rememberMeCheckBox)

        loginButton.setOnClickListener {
            performLogin()
        }

        registerTextView.setOnClickListener {
            // Beri tahu AuthActivity untuk menampilkan RegisterFragment
            setFragmentResult("auth_navigation_request", bundleOf("destination" to "register"))
        }

        forgotPasswordTextView.setOnClickListener {
            // Beri tahu AuthActivity untuk menampilkan ForgotPasswordFragment
            setFragmentResult("auth_navigation_request", bundleOf("destination" to "forgot_password"))
        }
    }

    private fun performLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Email dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d("LoginFragment", "signInWithEmail:success")
                    val user = firebaseAuth.currentUser
                    ToastUtils.showCustomToast(requireContext(), "Berhasil!", "Anda masuk sebagai ${user?.email}", CustomToastType.SUCCESS, Toast.LENGTH_SHORT)
                    // Beri tahu AuthActivity bahwa autentikasi berhasil
                    setFragmentResult("auth_navigation_request", bundleOf("destination" to "auth_success"))
                } else {
                    Log.w("LoginFragment", "signInWithEmail:failure", task.exception)
                    when (task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> Toast.makeText(context, "Kredensial tidak valid.", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(context, "Autentikasi gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}
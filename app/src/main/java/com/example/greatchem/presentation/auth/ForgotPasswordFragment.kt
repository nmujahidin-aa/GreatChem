package com.example.greatchem.presentation.auth

import com.example.greatchem.R
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
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class ForgotPasswordFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var resetButton: Button
    private lateinit var backToLoginTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = (requireActivity().application as App).firebaseAuth

        emailEditText = view.findViewById(R.id.emailEditText)
        resetButton = view.findViewById(R.id.resetButton)
        backToLoginTextView = view.findViewById(R.id.backToLoginTextView)

        resetButton.setOnClickListener {
            sendPasswordResetEmail()
        }

        backToLoginTextView.setOnClickListener {
            // Beri tahu AuthActivity untuk kembali ke LoginFragment
            setFragmentResult("forgot_password_navigation_request", bundleOf("destination" to "back_to_login"))
        }
    }

    private fun sendPasswordResetEmail() {
        val email = emailEditText.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(context, "Masukkan email Anda.", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d("ForgotPasswordFragment", "Password reset email sent.")
                    // Beri tahu AuthActivity bahwa email reset telah dikirim
                    setFragmentResult("forgot_password_navigation_request", bundleOf("destination" to "password_reset_sent"))
                } else {
                    Log.w("ForgotPasswordFragment", "sendPasswordResetEmail:failure", task.exception)
                    when (task.exception) {
                        is FirebaseAuthInvalidUserException -> Toast.makeText(context, "Tidak ada pengguna dengan email tersebut.", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(context, "Gagal mengirim email reset: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}
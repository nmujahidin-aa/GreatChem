package com.example.greatchem.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.greatchem.App
import com.example.greatchem.R
import com.example.greatchem.presentation.main.MainActivity
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        firebaseAuth = (application as App).firebaseAuth

        // Set listener untuk hasil dari fragment
        setupFragmentResultListeners()

        // Tampilkan LoginFragment pertama kali
        if (savedInstanceState == null) {
            loadFragment(LoginFragment())
        }
    }

    private fun setupFragmentResultListeners() {
        // Listener untuk permintaan navigasi dari LoginFragment
        supportFragmentManager.setFragmentResultListener(
            "auth_navigation_request",
            this
        ) { requestKey, bundle ->
            when (bundle.getString("destination")) {
                "register" -> loadFragment(RegisterFragment(), true)
                "forgot_password" -> loadFragment(ForgotPasswordFragment(), true)
                "auth_success" -> {
                    // Autentikasi berhasil (dari Login Fragment)
                    navigateToMainActivity()
                }
            }
        }

        // Listener untuk permintaan navigasi kembali dari RegisterFragment
        supportFragmentManager.setFragmentResultListener(
            "register_navigation_request",
            this
        ) { requestKey, bundle ->
            when (bundle.getString("destination")) {
                "back_to_login" -> supportFragmentManager.popBackStack() // Kembali ke LoginFragment
                "auth_success" -> {
                    // Pendaftaran berhasil
                    supportFragmentManager.popBackStack()
                }
            }
        }

        // Listener untuk permintaan navigasi kembali dari ForgotPasswordFragment
        supportFragmentManager.setFragmentResultListener(
            "forgot_password_navigation_request",
            this
        ) { requestKey, bundle ->
            when (bundle.getString("destination")) {
                "back_to_login" -> supportFragmentManager.popBackStack() // Kembali ke LoginFragment
                "password_reset_sent" -> {
                    Toast.makeText(this, "Link reset password telah dikirim ke email Anda.", Toast.LENGTH_LONG).show()
                    supportFragmentManager.popBackStack() // Kembali ke LoginFragment
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment, addToBackStack: Boolean = false) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null) // Tambahkan ke back stack agar bisa kembali dengan tombol back
        }
        transaction.commit()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@AuthActivity, MainActivity::class.java)
        startActivity(intent)
        finish() // Tutup AuthActivity
    }
}
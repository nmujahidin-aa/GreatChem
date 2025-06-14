package com.example.greatchem.presentation.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.greatchem.App
import com.example.greatchem.R
import com.example.greatchem.presentation.auth.AuthActivity
import com.example.greatchem.presentation.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay


class SplashActivity : AppCompatActivity() {
    private lateinit var viewModel: SplashViewModel
    private val splashDelay = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val auth = (application as App).firebaseAuth
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SplashViewModel(auth) as T
            }
        })[SplashViewModel::class.java]

        CoroutineScope(Dispatchers.Main).launch {
            delay(splashDelay)

            val intent = if (viewModel.isUserLoggedIn()) {
                Intent(this@SplashActivity, MainActivity::class.java)
            } else {
                Intent(this@SplashActivity, AuthActivity::class.java)
            }

            startActivity(intent)
            finish()
        }
    }
}
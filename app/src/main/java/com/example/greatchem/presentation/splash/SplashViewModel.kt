package com.example.greatchem.presentation.splash

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SplashViewModel(private val auth: FirebaseAuth) : ViewModel() {
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}
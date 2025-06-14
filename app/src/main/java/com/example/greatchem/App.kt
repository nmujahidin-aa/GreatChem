package com.example.greatchem

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage

class App : Application() {

    // Deklarasi komponen Firebase sebagai variabel global
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    lateinit var storage: FirebaseStorage

    override fun onCreate() {
        super.onCreate()
        // 1. Inisialisasi Firebase Core
        FirebaseApp.initializeApp(this)
        // 2. Inisialisasi komponen Firebase
        initFirebaseComponents()
        // 3. Konfigurasi tambahan (opsional)
        configureFirebaseSettings()
    }

    private fun initFirebaseComponents() {
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
    }

    private fun configureFirebaseSettings() {
        // Contoh konfigurasi Firestore
        val settings = firestore.firestoreSettings
        firestore.firestoreSettings = settings

        // Enable offline persistence
         FirebaseFirestoreSettings.Builder()
             .setPersistenceEnabled(true)
             .build()
    }
}
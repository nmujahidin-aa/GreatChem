package com.example.greatchem.presentation.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.greatchem.R
import com.example.greatchem.databinding.ActivityMainBinding
import com.example.greatchem.services.BacksoundMusicServices

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var musicIntent: Intent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Intent untuk Service musik
        musicIntent = Intent(this, BacksoundMusicServices::class.java)
        // Mulai Service musik saat MainActivity dibuat
        startService(musicIntent)

        // Setup Navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Hubungkan BottomNav dengan NavController
        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                // Daftar ID fragment utama Anda di mana bottom_nav harus terlihat
                // Ganti ini dengan R.id.id_fragment_anda_yang_ada_di_bottom_nav_menu
                R.id.homeFragment,
                R.id.gamesFragment,
                R.id.profileFragment -> {
                    binding.bottomNav.visibility = View.VISIBLE
                }
                else -> {
                    binding.bottomNav.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Hentikan Service musik saat MainActivity diha ncurkan
        stopService(musicIntent)
    }
}
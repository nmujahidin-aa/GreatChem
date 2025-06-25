package com.example.greatchem.presentation.profile


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.greatchem.R
import com.example.greatchem.databinding.FragmentProfileBinding
import com.example.greatchem.presentation.auth.AuthActivity
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // Inisialisasi FirebaseAuth
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi FirebaseAuth di onViewCreated
        auth = FirebaseAuth.getInstance()

        // Mengatur informasi pengguna
        val currentUser = auth.currentUser
        currentUser?.let {
            binding.username.text = it.displayName ?: "Nama Pengguna"
            binding.email.text = it.email ?: "email@gmail.com"
            // Jika Anda memiliki URL gambar profil, Anda bisa memuatnya di sini
            // menggunakan library seperti Glide atau Coil. Contoh:
            // Glide.with(this).load(it.photoUrl).into(binding.avatar)
        }

        // Menyiapkan listener untuk tombol Logout
        binding.logoutLayout.setOnClickListener { // Menggunakan ID yang benar dari XML: logout_layout
            showLogoutConfirmationDialog()
        }

        // --- Menyiapkan setiap item menu secara dinamis ---
        // Menggunakan fungsi helper setupMenuItem untuk setiap item
        setupMenuItem(binding.menuItemAccount.root, R.drawable.ic_account, "Ubah Profile") {
            findNavController().navigate(R.id.action_profileFragment_to_accountFragment)
        }
        setupMenuItem(binding.menuItemChangePassword.root, R.drawable.ic_encrypted, "Informasi Akun") {
            findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment)
        }
        setupMenuItem(binding.menuItemAboutUs.root, R.drawable.ic_info, "Tentang Kami") {
            findNavController().navigate(R.id.action_profileFragment_to_aboutUsFragment)
        }
        setupMenuItem(binding.menuItemContactUs.root, R.drawable.ic_mail, "Hubungi Kami") {
            findNavController().navigate(R.id.action_profileFragment_to_contactUsFragment)
        }
        setupMenuItem(binding.menuItemTermsOfService.root, R.drawable.ic_service, "Ketentuan Layanan") {
            findNavController().navigate(R.id.action_profileFragment_to_termsOfServiceFragment)
        }
        setupMenuItem(binding.menuItemPrivacyPolicy.root, R.drawable.ic_privacy, "Kebijakan Privasi") {
            findNavController().navigate(R.id.action_profileFragment_to_privacyPolicyFragment)
        }
    }

    // Fungsi helper untuk menampilkan dialog konfirmasi logout
    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin logout?")
            .setPositiveButton("Ya") { _, _ ->
                auth.signOut()
                val intent = Intent(requireContext(), AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish() // Tutup Activity induk Fragment ini
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    /**
     * Fungsi helper untuk mengatur ikon, teks, dan listener klik untuk item menu yang di-include.
     * Menggunakan View Binding untuk mengakses View dalam item yang di-include.
     * @param menuItemView Root View dari item menu yang di-include (misalnya, binding.menuItemAccount.root).
     * @param iconRes ID sumber daya drawable untuk ikon.
     * @param text Teks yang akan ditampilkan pada item menu.
     * @param onClick Lambda yang akan dijalankan saat item menu diklik.
     */
    private fun setupMenuItem(menuItemView: LinearLayout, iconRes: Int, text: String, onClick: () -> Unit) {
        // Mengakses ImageView dan TextView di dalam LinearLayout yang di-include melalui View Binding
        val iconView: ImageView = menuItemView.findViewById(R.id.menu_item_icon)
        val textView: TextView = menuItemView.findViewById(R.id.menu_item_text)

        // Mengatur ikon dan teks secara dinamis
        iconView.setImageResource(iconRes) // Mengatur ikon
        textView.text = text // Mengatur teks

        // Menambahkan listener klik
        menuItemView.setOnClickListener {
            onClick()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
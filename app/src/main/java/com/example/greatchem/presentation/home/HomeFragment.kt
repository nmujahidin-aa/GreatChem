package com.example.greatchem.presentation.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greatchem.R
import com.example.greatchem.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalTime

class HomeFragment : Fragment() {

    // Data class untuk merepresentasikan satu item menu
    data class MenuItem(
        val imageResId: Int,
        val title: String
    )

    private lateinit var searchEditText: EditText
    private lateinit var searchIcon: ImageView
    private lateinit var menuRecyclerView: RecyclerView
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Daftar menu asli, akan digunakan untuk memfilter
    private var originalMenuItems: List<MenuItem> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        currentUser?.let {
            binding.username.text = it.displayName ?: "Nama Pengguna"
        }
        binding.greetingTextView.text = getGreetingMessage()

        // 1. Siapkan daftar data menu asli
        originalMenuItems = listOf(
            // Contoh data sesuai dengan gambar yang Anda berikan
            MenuItem(R.drawable.card_petunjuk_media, "Petunjuk Media"),
            MenuItem(R.drawable.card_kompetensi, "Kompetensi"),
            MenuItem(R.drawable.card_peta_konsep, "Peta Konsep"),
            MenuItem(R.drawable.card_edukasi, "Edukasi"),
            MenuItem(R.drawable.card_scan_ar, "Scan AR"),
            MenuItem(R.drawable.card_pengembang, "Pengembang")
        )

        // 2. Dapatkan referensi ke View dari layout
        searchEditText = view.findViewById(R.id.searchEditText)
        searchIcon = view.findViewById(R.id.searchIcon)
        menuRecyclerView = view.findViewById(R.id.menuRecyclerView)

        // 3. Atur LayoutManager
        menuRecyclerView.layoutManager = GridLayoutManager(context, 2)

        // 4. Inisialisasi dan atur Adapter ke RecyclerView
        // Awalnya tampilkan semua item menu
        menuAdapter = MenuAdapter(originalMenuItems) { menuItem ->
            // Handle klik item menu di sini
            when (menuItem.title) {
                "Petunjuk Media" -> {
                    findNavController().navigate(R.id.mediaInstructionsFragment)
                }
                "Kompetensi" -> {
                    findNavController().navigate(R.id.competenceFragment)
                }
                "Peta Konsep" -> {
                     findNavController().navigate(R.id.mindMapFragment)
                }
                "Edukasi" -> {
                    findNavController().navigate(R.id.educationFragment)
                }
                "Scan AR" -> {
                    // TODO: Arahkan ke Scan AR fragment
                    // findNavController().navigate(R.id.scanARFragment)
                }
                "Pengembang" -> {
                     findNavController().navigate(R.id.developerInfoFragment)
                }
            }
        }
        menuRecyclerView.adapter = menuAdapter

        // 5. Tambahkan TextWatcher ke EditText untuk fitur pencarian
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Tidak perlu melakukan apa-apa sebelum teks berubah
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Panggil fungsi filter setiap kali teks berubah
                filterMenuItems(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                // Tidak perlu melakukan apa-apa setelah teks berubah
            }
        })

        // 6. Opsional: Tambahkan OnClickListener ke ikon pencarian jika Anda ingin tindakan spesifik saat diklik
        searchIcon.setOnClickListener {
            // Anda bisa menyembunyikan keyboard di sini atau memicu pencarian ulang
            // Contoh: menyembunyikan keyboard
            // val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            // imm.hideSoftInputFromWindow(view.windowToken, 0)
            filterMenuItems(searchEditText.text.toString())
        }
    }

    // Fungsi untuk memfilter item menu berdasarkan query pencarian
    private fun filterMenuItems(query: String) {
        val filteredList = if (query.isEmpty()) {
            originalMenuItems // Jika query kosong, tampilkan semua item asli
        } else {
            originalMenuItems.filter {
                // Ubah query dan judul item menjadi huruf kecil untuk pencarian case-insensitive
                it.title.toLowerCase().contains(query.toLowerCase())
            }
        }
        // Perbarui data di adapter
        menuAdapter.updateList(filteredList)
    }

    private fun getGreetingMessage(): String {
        val now = LocalTime.now()

        return when {
            now.isAfter(LocalTime.of(0, 0)) && now.isBefore(LocalTime.of(11, 0)) -> "Selamat Pagi"
            now.isAfter(LocalTime.of(11, 0)) && now.isBefore(LocalTime.of(15, 0)) -> "Selamat Siang"
            now.isAfter(LocalTime.of(15, 0)) && now.isBefore(LocalTime.of(19, 0)) -> "Selamat Sore"
            else -> "Selamat Malam"
        }
    }
}

package com.example.greatchem.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greatchem.R // Pastikan R Anda mengarah ke resources proyek Anda

class HomeFragment : Fragment() {

    // Data class untuk merepresentasikan satu item menu
    data class MenuItem(
        val imageResId: Int, // ID resource drawable untuk gambar
        val title: String,
        val subtitle: String
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout untuk fragmen ini
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Siapkan daftar data menu
        val menuItems = listOf(
            // Contoh data sesuai dengan gambar yang Anda berikan
            MenuItem(R.drawable.bg_auth, "Petunjuk Media", "hay"),
            MenuItem(R.drawable.bg_auth_2, "Kompetensi", "Eksplorasi"),
            MenuItem(R.drawable.bg_auth_2, "Peta Konsep", "Future Demo"),
            MenuItem(R.drawable.bg_auth_2, "Edukasi", "Mengukuhkan dengan AI"),
            MenuItem(R.drawable.bg_auth_2, "Scan AR", "Future Demo"),
            MenuItem(R.drawable.bg_auth_2, "Pengembang", "Visualisasi Ruang Angkasa")
            // Tambahkan item lain sesuai kebutuhan Anda
        )

        // 2. Dapatkan referensi ke RecyclerView dari layout
        val recyclerView = view.findViewById<RecyclerView>(R.id.menuRecyclerView)

        // 3. Atur LayoutManager
        // GridLayoutManager sudah didefinisikan di XML (app:layoutManager="androidx.recyclerview.widget.GridLayoutManager")
        // dan app:spanCount="2". Jadi, secara teori tidak perlu diatur lagi di sini,
        // namun, mengaturnya secara eksplisit memastikan konsistensi dan bisa membantu debugging.
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        // 4. Inisialisasi dan atur Adapter ke RecyclerView
        val adapter = MenuAdapter(menuItems) { menuItem ->
            // Callback ketika item diklik
            // Di sini Anda bisa menambahkan logika navigasi atau aksi lain
            // Misalnya: Toast.makeText(context, "Clicked: ${menuItem.title}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter
    }
}
package com.example.greatchem.presentation.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.greatchem.R // Pastikan R Anda mengarah ke resources proyek Anda

// Adapter untuk RecyclerView
class MenuAdapter(
    private val menuList: List<HomeFragment.MenuItem>, // Daftar data MenuItem
    private val onItemClick: (HomeFragment.MenuItem) -> Unit // Lambda untuk callback klik item
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    // ViewHolder adalah wadah untuk tampilan setiap item dalam daftar
    class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardImage: ImageView = itemView.findViewById(R.id.cardImage)
        val cardTitle: TextView = itemView.findViewById(R.id.cardTitle)
        // Jika tombol panah adalah ImageView, ganti ini:
        // val arrowIcon: ImageView = itemView.findViewById(R.id.arrowButton)
    }

    // Dipanggil ketika RecyclerView membutuhkan ViewHolder baru
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_menu_item, parent, false) // Inflate layout item kartu Anda
        // Jika nama layout Anda home_menu_item, ganti di sini:
        // .inflate(R.layout.home_menu_item, parent, false)
        return MenuViewHolder(view)
    }

    // Dipanggil oleh RecyclerView untuk menampilkan data pada posisi tertentu
    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val currentItem = menuList[position]

        holder.cardImage.setImageResource(currentItem.imageResId)
        holder.cardTitle.text = currentItem.title

        // Atur listener klik untuk seluruh item (atau tombol panah)
        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }

        // Contoh: Logika untuk ikon favorit (misalnya, mengubah gambar jika difavoritkan)
        // holder.favoriteIcon.setImageResource(if (currentItem.isFavorited) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline)
        // holder.favoriteIcon.setOnClickListener { /* Tambahkan logika favorit di sini */ }
    }

    // Mengembalikan jumlah total item dalam daftar
    override fun getItemCount(): Int {
        return menuList.size
    }
}
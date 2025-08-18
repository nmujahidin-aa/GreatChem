package com.example.greatchem.presentation.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.greatchem.R

// Adapter untuk RecyclerView
class MenuAdapter(
    private var menuList: List<HomeFragment.MenuItem>, // Mengubah dari val menjadi var
    private val onItemClick: (HomeFragment.MenuItem) -> Unit
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
            .inflate(R.layout.home_menu_item, parent, false)
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
    }

    // Mengembalikan jumlah total item dalam daftar
    override fun getItemCount(): Int {
        return menuList.size
    }

    /**
     * Memperbarui daftar item menu di adapter.
     * Setelah daftar diperbarui, notifyDataSetChanged() dipanggil untuk
     * memberi tahu RecyclerView agar me-render ulang tampilannya.
     *
     * @param newList Daftar item menu yang baru.
     */
    fun updateList(newList: List<HomeFragment.MenuItem>) {
        menuList = newList
        notifyDataSetChanged() // Memberi tahu RecyclerView bahwa datanya telah berubah
    }
}

package com.example.greatchem.presentation.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.greatchem.R
import com.example.greatchem.databinding.FragmentEducationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EducationFragment : Fragment() {

    // Deklarasi binding
    private var _binding: FragmentEducationBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout menggunakan binding
        _binding = FragmentEducationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup Toolbar menggunakan binding
        val toolbar: Toolbar = binding.toolbar
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Setup Card untuk Video Pembelajaran
        // Mengakses CardView menggunakan findViewById pada root view fragment
        setupMenuCard(
            view.findViewById(R.id.include_card_video_pembelajaran),
            R.drawable.bg_menu_video_pembelajaran_2,
            R.drawable.ic_video,
            "Video Pembelajaran",
            {
                println("Video Pembelajaran diklik!")
                 findNavController().navigate(R.id.action_educationFragment_to_learningVideosFragment)
            }
        )

        setupMenuCard(
            view.findViewById(R.id.include_card_virtual_lab),
            R.drawable.bg_menu_virtual_lab_2,
            R.drawable.ic_virtual_lab,
            "Virtual-Lab",
            {
                println("Virtual-Lab diklik!")
                findNavController().navigate(R.id.action_educationFragment_to_virtualLabFragment)
            }
        )

        setupMenuCard(
            binding.includeCardLatihanSoal.root,
            R.drawable.bg_menu_latihan_soal_2,
            R.drawable.ic_exercise,
            "Latihan Soal",
            {
                checkQuizStatusAndProceed()
            }
        )
    }

    /**
     * Fungsi pembantu untuk mengatur properti kartu menu secara dinamis.
     * @param cardView CardView yang ingin diatur.
     * @param backgroundResId ID drawable untuk gambar latar belakang.
     * @param iconResId ID drawable untuk ikon.
     * @param titleText Teks judul menu.
     * @param onClickListener Lambda yang akan dijalankan saat kartu diklik.
     */
    private fun setupMenuCard(
        cardView: CardView,
        backgroundResId: Int,
        iconResId: Int,
        titleText: String,
        onClickListener: () -> Unit
    ) {
        val backgroundImage = cardView.findViewById<ImageView>(R.id.card_background_image)
        val iconImage = cardView.findViewById<ImageView>(R.id.card_icon)
        val titleTextView = cardView.findViewById<TextView>(R.id.card_title)

        backgroundImage.setImageResource(backgroundResId)
        iconImage.setImageResource(iconResId)
        titleTextView.text = titleText
        cardView.setOnClickListener { onClickListener.invoke() }
    }

    private fun checkQuizStatusAndProceed() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(context, "Anda harus login untuk mengakses kuis.", Toast.LENGTH_SHORT).show()
            // Anda bisa tambahkan navigasi ke layar login di sini jika diinginkan
            return
        }

        val userId = currentUser.uid
        firestore.collection("user_quiz_scores").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val score = document.getLong("score")?.toInt() ?: 0
                    val passed = document.getBoolean("passed") ?: false

                    val bundle = Bundle().apply {
                        putInt("score", score)
                        putBoolean("passed", passed)
                        // canRetake hanya true jika belum lulus
                        putBoolean("canRetake", !passed)
                    }
                    // Navigasi ke ExerciseResultFragment dengan membawa data skor
                    findNavController().navigate(R.id.action_educationFragment_to_exerciseResultFragment, bundle)

                } else {
                    // Belum pernah mengerjakan kuis, langsung mulai kuis
                    startExerciseFragment()
                }
            }
            .addOnFailureListener { e ->
                Log.e("EducationFragment", "Error checking quiz status: ${e.message}", e)
                Toast.makeText(context, "Gagal memeriksa status kuis. Silakan coba lagi.", Toast.LENGTH_SHORT).show()
                // Jika ada error, tetap izinkan untuk mencoba kuis (asumsi belum pernah)
                startExerciseFragment()
            }
    }

    private fun startExerciseFragment() {
        findNavController().navigate(R.id.action_educationFragment_to_exerciseFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        super.onDestroy()
        _binding = null
    }
}

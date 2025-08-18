package com.example.greatchem.presentation.home.education.exercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.greatchem.R

class ExerciseResultFragment : Fragment() {

    private lateinit var tvResultTitle: TextView
    private lateinit var tvLargeScore: TextView
    private lateinit var tvPreviousScoreLabel: TextView
    private lateinit var tvScoreFraction: TextView
    private lateinit var tvStatus: TextView // Ditambahkan kembali
    private lateinit var btnRetakeQuiz: Button
    private lateinit var btnBackToEducation: Button // Ditambahkan kembali

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_education_exercise_result, container, false)

        // Inisialisasi View
        tvResultTitle = view.findViewById(R.id.tv_result_title)
        tvLargeScore = view.findViewById(R.id.tv_large_score)
        tvPreviousScoreLabel = view.findViewById(R.id.tv_previous_score_label)
        tvScoreFraction = view.findViewById(R.id.tv_score_fraction)
        tvStatus = view.findViewById(R.id.tv_status) // Inisialisasi tvStatus
        btnRetakeQuiz = view.findViewById(R.id.btn_retake_quiz)
        btnBackToEducation = view.findViewById(R.id.btn_back_to_education) // Inisialisasi btnBackToEducation

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil data dari arguments (Bundle)
        val score = arguments?.getInt("score", 0) ?: 0
        val passed = arguments?.getBoolean("passed", false) ?: false
        val canRetake = arguments?.getBoolean("canRetake", false) ?: false

        // Atur teks sesuai skor yang didapat
        tvLargeScore.text = score.toString()
        tvScoreFraction.text = "$score/100"

        // Atur Status
        if (passed) {
            tvStatus.text = "Status: Selamat! Anda Lulus!"
            // Sembunyikan tombol "Pergi Tes Lagi" jika sudah lulus
            btnRetakeQuiz.visibility = View.GONE
        } else {
            tvStatus.text = "Status: Maaf, Anda Belum Lulus."
            // Tampilkan tombol "Pergi Tes Lagi" jika belum lulus dan diizinkan mengulang
            if (canRetake) {
                btnRetakeQuiz.visibility = View.VISIBLE
            } else {
                // Sembunyikan jika tidak diizinkan mengulang (misal: jika ada logika lain yang melarang)
                btnRetakeQuiz.visibility = View.GONE
            }
        }

        btnRetakeQuiz.setOnClickListener {
            // Arahkan kembali ke ExerciseFragment untuk mengulang tes
            // Menggunakan popUpTo untuk membersihkan back stack dan memulai kuis baru
            findNavController().navigate(R.id.action_exerciseResultFragment_to_exerciseFragment)
        }

        btnBackToEducation.setOnClickListener {
            // Kembali ke EducationFragment (atau pop back ke sana)
            // popUpTo educationFragment (ID dari nav graph), dan popUpToInclusive = false
            // artinya educationFragment sendiri tidak di-pop, hanya fragment di atasnya
            findNavController().popBackStack(R.id.educationFragment, false)
        }
    }
}
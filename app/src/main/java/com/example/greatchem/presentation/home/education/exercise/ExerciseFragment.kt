package com.example.greatchem.presentation.home.education.exercise

import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.greatchem.R
import com.example.greatchem.data.Content
import com.example.greatchem.data.Question
import com.example.greatchem.data.QuizDataSource
import com.example.greatchem.services.BacksoundMusicServices
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ExerciseFragment : Fragment() {

    // Deklarasi View
    private lateinit var ivQuestionImage: ImageView
    private lateinit var tvQuestionProgress: TextView
    private lateinit var tvQuestionText: TextView
    private lateinit var radioGroupOptions: RadioGroup
    private lateinit var rbOptionA: RadioButton
    private lateinit var rbOptionB: RadioButton
    private lateinit var rbOptionC: RadioButton
    private lateinit var rbOptionD: RadioButton
    private lateinit var rbOptionE: RadioButton
    private lateinit var btnCheckAnswer: Button
    private lateinit var tvFeedback: TextView
    private lateinit var btnNextQuestionMain: Button // Tombol 'Lanjutkan' di bawah feedback

    // View untuk Overlay Penjelasan
    private lateinit var explanationOverlay: FrameLayout
    private lateinit var tvExplanationTitle: TextView
    private lateinit var tvExplanationOverlay: TextView
    private lateinit var ivExplanationImageOverlay: ImageView
    private lateinit var btnNextQuestionOverlay: Button

    // Data Kuis
    private lateinit var questions: List<Question>
    private var currentQuestionIndex = 0
    private var correctAnswersCount = 0

    // MediaPlayer untuk SFX
    private var mediaPlayerSfx: MediaPlayer? = null

    // Firebase
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_education_excercise, container, false)

        // Inisialisasi semua View Utama
        ivQuestionImage = view.findViewById(R.id.ivQuestionImage)
        tvQuestionProgress = view.findViewById(R.id.tv_question_progress)
        tvQuestionText = view.findViewById(R.id.tv_question_text)
        radioGroupOptions = view.findViewById(R.id.radio_group_options)
        rbOptionA = view.findViewById(R.id.rb_option_a)
        rbOptionB = view.findViewById(R.id.rb_option_b)
        rbOptionC = view.findViewById(R.id.rb_option_c)
        rbOptionD = view.findViewById(R.id.rb_option_d)
        rbOptionE = view.findViewById(R.id.rb_option_e)
        btnCheckAnswer = view.findViewById(R.id.btn_check_answer)
        tvFeedback = view.findViewById(R.id.tv_feedback)
        btnNextQuestionMain = view.findViewById(R.id.btn_next_question)

        // Inisialisasi View Overlay
        explanationOverlay = view.findViewById(R.id.explanation_overlay)
        tvExplanationTitle = view.findViewById(R.id.tv_explanation_title)
        tvExplanationOverlay = view.findViewById(R.id.tv_explanation_text_overlay)
        ivExplanationImageOverlay = view.findViewById(R.id.iv_explanation_image_overlay)
        btnNextQuestionOverlay = view.findViewById(R.id.btn_next_question_overlay)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stopBacksoundService()

        questions = QuizDataSource.getQuestions()
        if (questions.isEmpty()) {
            Toast.makeText(context, "Soal kuis tidak ditemukan!", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
            return
        }

        displayQuestion()

        btnCheckAnswer.setOnClickListener {
            checkAnswer()
        }

        btnNextQuestionMain.setOnClickListener {
            nextQuestion()
        }

        btnNextQuestionOverlay.setOnClickListener {
            nextQuestion()
        }

        radioGroupOptions.setOnCheckedChangeListener { _, checkedId ->
            btnCheckAnswer.isEnabled = checkedId != -1
        }
        btnCheckAnswer.isEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        startBacksoundService()
        releaseSfxMediaPlayer()
    }

    private fun stopBacksoundService() {
        context?.let {
            val intent = Intent(it, BacksoundMusicServices::class.java)
            it.stopService(intent)
            Log.d("ExerciseFragment", "Backsound music stopped.")
        }
    }

    private fun startBacksoundService() {
        context?.let {
            val intent = Intent(it, BacksoundMusicServices::class.java)
            it.startService(intent)
            Log.d("ExerciseFragment", "Backsound music started.")
        }
    }

    private fun playSfx(rawResId: Int) {
        releaseSfxMediaPlayer()

        context?.let {
            mediaPlayerSfx = MediaPlayer.create(it, rawResId)
            mediaPlayerSfx?.setOnCompletionListener { mp ->
                mp.release()
                mediaPlayerSfx = null
            }
            mediaPlayerSfx?.start()
        }
    }

    private fun releaseSfxMediaPlayer() {
        mediaPlayerSfx?.stop()
        mediaPlayerSfx?.release()
        mediaPlayerSfx = null
    }

    private fun displayQuestion() {
        val currentQuestion = questions[currentQuestionIndex]

        tvQuestionProgress.text = "Soal ${currentQuestionIndex + 1}/${questions.size}"

        if (currentQuestion.questionImageResId != 0) {
            ivQuestionImage.setImageResource(currentQuestion.questionImageResId)
            ivQuestionImage.visibility = View.VISIBLE
            tvQuestionText.visibility = View.GONE
        } else {
            ivQuestionImage.visibility = View.GONE
            tvQuestionText.visibility = View.VISIBLE
        }

        fun setOptionContent(radioButton: RadioButton, content: Content) {
            when (content) {
                is Content.Text -> {
                    radioButton.text = content.value
                    // Hapus drawable jika sebelumnya ada
                    radioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                }
                is Content.Image -> {
                    val drawable = context?.let {
                        val img = it.resources.getDrawable(content.resId, it.theme)
                        // Atur ukuran drawable
                        val newWidth = 300
                        val newHeight = 300
                        img?.setBounds(0, 0, newWidth, newHeight)
                        img
                    }
                    radioButton.text = ""
                    radioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
                }
            }
        }

        setOptionContent(rbOptionA, currentQuestion.optionA)
        setOptionContent(rbOptionB, currentQuestion.optionB)
        setOptionContent(rbOptionC, currentQuestion.optionC)
        setOptionContent(rbOptionD, currentQuestion.optionD)
        setOptionContent(rbOptionE, currentQuestion.optionE)

        // Reset tampilan
        radioGroupOptions.clearCheck()
        resetOptionColors()
        tvFeedback.visibility = View.GONE
        btnCheckAnswer.visibility = View.VISIBLE
        btnNextQuestionMain.visibility = View.GONE
        explanationOverlay.visibility = View.GONE
        btnCheckAnswer.isEnabled = false
        radioGroupOptions.isEnabled = true
    }

    private fun checkAnswer() {
        val selectedOptionId = radioGroupOptions.checkedRadioButtonId
        if (selectedOptionId == -1) return

        val selectedAnswer = when (selectedOptionId) {
            R.id.rb_option_a -> "A"
            R.id.rb_option_b -> "B"
            R.id.rb_option_c -> "C"
            R.id.rb_option_d -> "D"
            R.id.rb_option_e -> "E"
            else -> ""
        }

        val currentQuestion = questions[currentQuestionIndex]

        btnCheckAnswer.visibility = View.GONE
        radioGroupOptions.isEnabled = false

        if (selectedAnswer == currentQuestion.correctAnswer) {
            correctAnswersCount++
            tvFeedback.text = "Benar! ✅"
            tvFeedback.setTextColor(Color.GREEN)
            tvFeedback.visibility = View.VISIBLE
            btnNextQuestionMain.visibility = View.VISIBLE
            playSfx(R.raw.correct)
        } else {
            tvFeedback.text = "Salah! ❌"
            tvFeedback.setTextColor(Color.RED)
            tvFeedback.visibility = View.VISIBLE

            // Highlight jawaban yang benar dengan warna hijau
            val correctAnswerRadioButton = when (currentQuestion.correctAnswer) {
                "A" -> rbOptionA
                "B" -> rbOptionB
                "C" -> rbOptionC
                "D" -> rbOptionD
                "E" -> rbOptionE
                else -> null
            }
            correctAnswerRadioButton?.setBackgroundResource(R.drawable.bg_option_correct)

            playSfx(R.raw.wrong)

            showExplanationOverlay(currentQuestion.explanation)
        }
    }

    private fun showExplanationOverlay(explanation: Content) {
        when (explanation) {
            is Content.Text -> {
                tvExplanationOverlay.text = explanation.value
                tvExplanationOverlay.visibility = View.VISIBLE
                ivExplanationImageOverlay.visibility = View.GONE
            }
            is Content.Image -> {
                ivExplanationImageOverlay.setImageResource(explanation.resId)
                ivExplanationImageOverlay.visibility = View.VISIBLE
                tvExplanationOverlay.visibility = View.GONE
            }
        }
        explanationOverlay.visibility = View.VISIBLE
    }

    private fun nextQuestion() {
        explanationOverlay.visibility = View.GONE // Sembunyikan overlay
        currentQuestionIndex++
        if (currentQuestionIndex < questions.size) {
            displayQuestion()
        } else {
            calculateAndSaveResult()
        }
    }

    private fun calculateAndSaveResult() {
        val totalQuestions = questions.size
        val finalScore = (correctAnswersCount.toDouble() / totalQuestions * 100).toInt()
        val passed = finalScore >= 80

        saveQuizResultToFirestore(finalScore, passed)

        val bundle = Bundle().apply {
            putInt("score", finalScore)
            putBoolean("passed", passed)
            putBoolean("canRetake", !passed)
        }
        findNavController().navigate(R.id.action_exerciseFragment_to_exerciseResultFragment, bundle)
    }

    private fun saveQuizResultToFirestore(finalScore: Int, passed: Boolean) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val quizResult = hashMapOf(
                "userId" to userId,
                "score" to finalScore,
                "passed" to passed,
                "lastAttemptDate" to Timestamp.now()
            )

            firestore.collection("user_quiz_scores").document(userId)
                .set(quizResult)
                .addOnSuccessListener {
                    Log.d("ExerciseFragment", "Quiz result saved successfully for user $userId")
                }
                .addOnFailureListener { e ->
                    Log.w("ExerciseFragment", "Error saving quiz result for user $userId", e)
                    Toast.makeText(context, "Gagal menyimpan hasil kuis.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "Anda tidak login. Hasil tidak disimpan.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetOptionColors() {
        rbOptionA.setBackgroundResource(R.drawable.bg_option_card_selector)
        rbOptionB.setBackgroundResource(R.drawable.bg_option_card_selector)
        rbOptionC.setBackgroundResource(R.drawable.bg_option_card_selector)
        rbOptionD.setBackgroundResource(R.drawable.bg_option_card_selector)
        rbOptionE.setBackgroundResource(R.drawable.bg_option_card_selector)
    }
}
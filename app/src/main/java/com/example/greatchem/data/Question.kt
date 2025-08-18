package com.example.greatchem.data

import com.example.greatchem.R


sealed class Content {
    data class Text(val value: String) : Content()
    data class Image(val resId: Int) : Content()
}

data class Question(
    val id: String,
    val questionImageResId: Int,
    val optionA: Content,
    val optionB: Content,
    val optionC: Content,
    val optionD: Content,
    val optionE: Content,
    val correctAnswer: String,
    val explanation: Content
)

object QuizDataSource {

    fun getQuestions(): List<Question> {
        return listOf(
            Question(
                id = "q1",
                questionImageResId = R.drawable.soal_1,
                optionA = Content.Text("1 menjadi 2"),
                optionB = Content.Text("1 menjadi 3"),
                optionC = Content.Text("2 menjadi 4"),
                optionD = Content.Text("4 menjadi 5"),
                optionE = Content.Text("3 menjadi 5"),
                correctAnswer = "C",
                explanation = Content.Text("Karena perbedaan hanya pada bentuk partikel (luas permukaan), tanpa perlu menambah energi (pemanasan), sehingga mendukung prinsip Green Chemistry: \"Efisiensi energi dan proses yang lebih aman\"")
            ),
            Question(
                id = "q2",
                questionImageResId = R.drawable.soal_2,
                optionA = Content.Text("1/36"),
                optionB = Content.Text("1/18"),
                optionC = Content.Text("5/36"),
                optionD = Content.Text("5/18"),
                optionE = Content.Text("5/9"),
                correctAnswer = "E",
                explanation = Content.Image(R.drawable.explanation2),
            ),
            Question(
                id = "q3",
                questionImageResId = R.drawable.soal_3,
                optionA = Content.Text("1 Terhadap 2"),
                optionB = Content.Text("1 Terhadap 3"),
                optionC = Content.Text("2 Terhadap 4"),
                optionD = Content.Text("3 Terhadap 3"),
                optionE = Content.Text("4 Terhadap 5"),
                correctAnswer = "A",
                explanation = Content.Text("Hanya tabung 1 dan 2 yang memiliki variabel konsentrasi sebagai satu-satunya faktor yang berubah."),
            ),
            Question(
                id = "q4",
                questionImageResId = R.drawable.soal_4,
                optionA = Content.Text("2,20 mL·detik⁻¹"),
                optionB = Content.Text("2,50 mL·detik⁻¹"),
                optionC = Content.Text("2,80 mL·detik⁻¹"),
                optionD = Content.Text("3,40 mL·detik⁻¹"),
                optionE = Content.Text("4,80 mL·detik⁻¹"),
                correctAnswer = "D",
                explanation = Content.Image(R.drawable.explanation4),
            ),
            Question(
                id = "q5",
                questionImageResId = R.drawable.soal_5,
                optionA = Content.Text("(1) dan (2)"),
                optionB = Content.Text("(1) dan (3)"),
                optionC = Content.Text("(2) dan (3)"),
                optionD = Content.Text("(2) dan (4)"),
                optionE = Content.Text("(3) dan (4)"),
                correctAnswer = "A",
                explanation = Content.Image(R.drawable.explanation5),
            ),
            Question(
                id = "q6",
                questionImageResId = R.drawable.soal_6,
                optionA = Content.Text("Konsentrasi"),
                optionB = Content.Text("Suhu"),
                optionC = Content.Text("Luas Permukaan"),
                optionD = Content.Text("Katalis"),
                optionE = Content.Text("Waktu"),
                correctAnswer = "B",
                explanation = Content.Text("Karena kenaikan suhu mempercepat laju reaksi, sehingga waktu reaksi menjadi lebih singkat."),
            ),
            Question(
                id = "q7",
                questionImageResId = R.drawable.soal_7,
                optionA = Content.Text("1 dan 2"),
                optionB = Content.Text("2 dan 3"),
                optionC = Content.Text("2 dan 4"),
                optionD = Content.Text("3 dan 4"),
                optionE = Content.Text("4 dan 5"),
                correctAnswer = "C",
                explanation = Content.Image(R.drawable.explanation7),
            ),
            Question(
                id = "q8",
                questionImageResId = R.drawable.soal_8,
                optionA = Content.Text("Konsentrasi HCl, luas permukaan loga Mg, laju reaksi"),
                optionB = Content.Text("Konsentrasi HCl, laju reaksi, luas permukaan logam Mg"),
                optionC = Content.Text("Luas permukaan logam Mg, konsentrasi HC;, laju reaksi"),
                optionD = Content.Text("Laju reaksi, konsentrasi HCl, luas permukaan logam Mg"),
                optionE = Content.Text("Laju reaksi, luas permukaan logam Mg, konsentrasi HCl"),
                correctAnswer = "A",
                explanation = Content.Image(R.drawable.explanation8),
            ),
            Question(
                id = "q9",
                questionImageResId = R.drawable.soal_9,
                optionA = Content.Text("0,83 mL/detik"),
                optionB = Content.Text("1,33 mL/detik"),
                optionC = Content.Text("2,67 mL/detik"),
                optionD = Content.Text("2,50 mL/detik"),
                optionE = Content.Text("7,50 mL/detik"),
                correctAnswer = "C",
                explanation = Content.Image(R.drawable.explanation9),
            ),
            Question(
                id = "q10",
                questionImageResId = R.drawable.soal_10,
                optionA = Content.Image(R.drawable.option10_a),
                optionB = Content.Image(R.drawable.option10_b),
                optionC = Content.Image(R.drawable.option10_c),
                optionD = Content.Image(R.drawable.option10_d),
                optionE = Content.Image(R.drawable.option10_e),
                correctAnswer = "C",
                explanation = Content.Image(R.drawable.explanation10),
            )
        )
    }
}
package com.example.greatchem.presentation.home.education

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greatchem.R
import com.example.greatchem.data.Video
import com.example.greatchem.presentation.education.adapter.VideoAdapter // Pastikan path ini benar
import com.google.firebase.firestore.FirebaseFirestore

class LearningVideosFragment : Fragment() {

    private lateinit var recyclerViewVideos: RecyclerView
    private lateinit var videoAdapter: VideoAdapter
    private val firestore = FirebaseFirestore.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_education_learning_videos, container, false)

        recyclerViewVideos = view.findViewById(R.id.recyclerViewVideos)
        recyclerViewVideos.layoutManager = LinearLayoutManager(context)

        videoAdapter = VideoAdapter(emptyList()) { video ->
            val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                putExtra("videoUrl", video.link)
                putExtra("videoTitle", video.title)
                putExtra("videoDescription", video.description)
            }
            startActivity(intent)
        }
        recyclerViewVideos.adapter = videoAdapter

        loadVideosFromFirestore()

        return view
    }

    private fun loadVideosFromFirestore() {
        firestore.collection("learning_videos")
            .get()
            .addOnSuccessListener { result ->
                val videoList = mutableListOf<Video>()
                for (document in result) {
                    try {
                        val video = document.toObject(Video::class.java)
                        videoList.add(video)
                    } catch (e: Exception) {
                        Log.e("FirestoreError", "Error converting document to Video: ${document.id}", e)
                        Toast.makeText(context, "Error memuat video: ${document.id}", Toast.LENGTH_SHORT).show()
                    }
                }
                videoAdapter.updateVideos(videoList)
                Log.d("LearningVideosFragment", "Videos loaded: ${videoList.size}")
            }
            .addOnFailureListener { exception ->
                Log.w("LearningVideosFragment", "Error getting documents: ", exception)
                Toast.makeText(context, "Gagal memuat video: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}
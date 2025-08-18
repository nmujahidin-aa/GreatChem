package com.example.greatchem.presentation.home.education

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.example.greatchem.R

class VideoPlayerActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        playerView = findViewById(R.id.player_view)

        val videoUrl = intent.getStringExtra("videoUrl")
        val videoTitle = intent.getStringExtra("videoTitle")

        supportActionBar?.title = videoTitle

        if (videoUrl != null) {
            initializePlayer(videoUrl)
        }
    }

    private fun initializePlayer(videoUrl: String) {
        player = ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                playerView.player = exoPlayer
                val mediaItem = MediaItem.fromUri(videoUrl)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentItem, playbackPosition)
                exoPlayer.prepare()
            }
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            currentItem = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        player = null
    }

    override fun onStart() {
        super.onStart()
        // If API level is 24 or higher, initialize player in onStart.
        // For older APIs, initialize in onCreate.
        if (player == null) {
            val videoUrl = intent.getStringExtra("videoUrl")
            if (videoUrl != null) {
                initializePlayer(videoUrl)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // For older APIs, if player was null in onStart due to multi-window or similar.
        if (player == null) {
            val videoUrl = intent.getStringExtra("videoUrl")
            if (videoUrl != null) {
                initializePlayer(videoUrl)
            }
        }
        player?.playWhenReady = playWhenReady // Resume playback
    }

    override fun onPause() {
        super.onPause()
        // Release the player if app goes to background
        releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        // Release the player
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }
}
package com.example.greatchem.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import com.example.greatchem.R

class BacksoundMusicServices : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("BacksoundService", "Service created, initializing MediaPlayer.")
        mediaPlayer = MediaPlayer.create(this, R.raw.backsound)
        mediaPlayer?.isLooping = true
        mediaPlayer?.setVolume(0.5f, 0.5f)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("BacksoundService", "Service onStartCommand, starting music.")
        mediaPlayer?.start()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("BacksoundService", "Service onDestroy, stopping and releasing MediaPlayer.")
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
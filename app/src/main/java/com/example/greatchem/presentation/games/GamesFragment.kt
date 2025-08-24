package com.example.greatchem.presentation.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.greatchem.R
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.firebase.auth.FirebaseAuth

class GamesFragment : Fragment() {
    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_games, container, false)
        webView = view.findViewById(R.id.webView)

        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_DEFAULT

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val baseUrl = "https://greatchem-games.netlify.app/"

        if (userId != null) {
            webView.loadUrl("$baseUrl?userId=$userId")
        } else {
            webView.loadUrl(baseUrl)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        webView.destroy()
    }
}

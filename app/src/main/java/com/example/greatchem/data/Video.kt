package com.example.greatchem.data

import com.google.firebase.firestore.DocumentId

data class Video(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val link: String = "",
    val thumbnail: String = "",
    val description: String = ""
) {
    fun getVideoUrl(): String {
        return link
    }

    fun getThumbnailUrl(): String {
        return thumbnail
    }
}
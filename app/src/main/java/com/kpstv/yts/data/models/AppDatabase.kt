package com.kpstv.yts.data.models

import com.google.gson.annotations.SerializedName

data class AppDatabase(
    val misc: Misc,
    val tmdb: Tmdb,
    val yts: Yts,
    val update: Update,
    val message: Message
) {
    data class Update(
        val deprecatedVersionCode: Int,
        val versionCode: Int,
        val url: String
    )

    data class Misc(
        val suggestionApi: String
    )

    data class Tmdb(
        val apiKey: String,
        val base: String,
        val image: String
    )

    data class Yts(
        val base: String,
        val yify: String
    )

    // All message are html text
    data class Message(
        @SerializedName("Final-onStart")
        val finalOnStart: String,
        @SerializedName("Search-onStart")
        val searchOnStart: String,
        @SerializedName("Splash-onStart")
        val splashOnStart: String
    )
}
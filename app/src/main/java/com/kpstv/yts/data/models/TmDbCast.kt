package com.kpstv.yts.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "table_cast")
data class TmDbCastDomain(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val imdbCode: String,
    val cast: TmDbCast
)

data class TmDbCast(
    @SerializedName("cast_id")
    val castId: Int,
    val character: String,
    @SerializedName("credit_id")
    val creditId: String,
    val gender: Int,
    @SerializedName("id")
    val personId: Int,
    val name: String,
    val order: Int,
    @SerializedName("profile_path")
    val profilePath: String
)

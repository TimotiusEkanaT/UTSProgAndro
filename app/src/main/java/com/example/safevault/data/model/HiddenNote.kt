package com.example.safevault.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "hidden_notes")
data class HiddenNote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val encryptionKey: String? = null, // For additional encryption
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val userId: String = "", // Firebase user ID
    val accessKeyword: String = "" // Keyword to reveal hidden note
) : Parcelable

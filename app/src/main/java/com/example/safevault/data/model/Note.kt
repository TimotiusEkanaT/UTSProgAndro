package com.example.safevault.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val isHidden: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val userId: String = "" // Firebase user ID
) : Parcelable

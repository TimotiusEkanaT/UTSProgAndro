package com.example.safevault.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String, // Firebase Auth UID
    val email: String,
    val displayName: String? = null,
    val hasFingerprintData: Boolean = false,
    val hasFaceIdData: Boolean = false,
    val hasPin: Boolean = false,
    val pin: String? = null, // Should be encrypted
    val hiddenNoteKeyword: String? = null, // Keyword for hidden notes
    val hiddenNoteEnabled: Boolean = false
)

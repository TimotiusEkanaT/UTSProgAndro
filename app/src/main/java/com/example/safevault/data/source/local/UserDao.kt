package com.example.safevault.data.source.local

import androidx.room.*
import com.example.safevault.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("UPDATE users SET hasFingerprintData = :hasFingerprint WHERE id = :userId")
    suspend fun updateFingerprintStatus(userId: String, hasFingerprint: Boolean)

    @Query("UPDATE users SET hasFaceIdData = :hasFaceId WHERE id = :userId")
    suspend fun updateFaceIdStatus(userId: String, hasFaceId: Boolean)

    @Query("UPDATE users SET hasPin = :hasPin, pin = :pin WHERE id = :userId")
    suspend fun updatePinStatus(userId: String, hasPin: Boolean, pin: String?)

    @Query("UPDATE users SET hiddenNoteEnabled = :enabled, hiddenNoteKeyword = :keyword WHERE id = :userId")
    suspend fun updateHiddenNoteSettings(userId: String, enabled: Boolean, keyword: String?)
}

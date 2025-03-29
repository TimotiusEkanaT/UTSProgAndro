package com.example.safevault.data.source.local

import androidx.room.*
import com.example.safevault.data.model.Note
import com.example.safevault.data.model.HiddenNote
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    // Regular notes
    @Query("SELECT * FROM notes WHERE userId = :userId AND isHidden = 0 ORDER BY updatedAt DESC")
    fun getAllNotes(userId: String): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM notes WHERE userId = :userId AND isHidden = 0 AND (title LIKE '%' || :searchQuery || '%' OR content LIKE '%' || :searchQuery || '%')")
    fun searchNotes(userId: String, searchQuery: String): Flow<List<Note>>

    // Hidden notes
    @Query("SELECT * FROM hidden_notes WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getAllHiddenNotes(userId: String): Flow<List<HiddenNote>>

    @Query("SELECT * FROM hidden_notes WHERE id = :id")
    suspend fun getHiddenNoteById(id: Long): HiddenNote?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHiddenNote(hiddenNote: HiddenNote): Long

    @Update
    suspend fun updateHiddenNote(hiddenNote: HiddenNote)

    @Delete
    suspend fun deleteHiddenNote(hiddenNote: HiddenNote)

    @Query("SELECT * FROM hidden_notes WHERE userId = :userId AND (title LIKE '%' || :searchQuery || '%' OR content LIKE '%' || :searchQuery || '%')")
    fun searchHiddenNotes(userId: String, searchQuery: String): Flow<List<HiddenNote>>
}

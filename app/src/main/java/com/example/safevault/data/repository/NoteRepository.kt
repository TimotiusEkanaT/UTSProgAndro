package com.example.safevault.data.repository

import com.example.safevault.data.model.Note
import com.example.safevault.data.model.HiddenNote
import com.example.safevault.data.source.local.NoteDao
import com.example.safevault.utils.Encryption
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val encryption: Encryption
) {
    // Regular notes
    fun getAllNotes(userId: String): Flow<List<Note>> {
        return noteDao.getAllNotes(userId)
    }

    suspend fun getNoteById(id: Long): Note? {
        return noteDao.getNoteById(id)
    }

    suspend fun insertNote(note: Note): Long {
        return noteDao.insertNote(note)
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }

    fun searchNotes(userId: String, query: String): Flow<List<Note>> {
        return noteDao.searchNotes(userId, query)
    }

    // Hidden notes
    fun getAllHiddenNotes(userId: String): Flow<List<HiddenNote>> {
        return noteDao.getAllHiddenNotes(userId)
    }

    suspend fun getHiddenNoteById(id: Long): HiddenNote? {
        return noteDao.getHiddenNoteById(id)
    }

    suspend fun insertHiddenNote(hiddenNote: HiddenNote, rawContent: String): Long {
        // Encrypt content before saving
        val encryptedContent = encryption.encrypt(rawContent)
        val noteWithEncryptedContent = hiddenNote.copy(content = encryptedContent)
        return noteDao.insertHiddenNote(noteWithEncryptedContent)
    }

    suspend fun updateHiddenNote(hiddenNote: HiddenNote, rawContent: String) {
        // Encrypt content before saving
        val encryptedContent = encryption.encrypt(rawContent)
        val noteWithEncryptedContent = hiddenNote.copy(content = encryptedContent)
        noteDao.updateHiddenNote(noteWithEncryptedContent)
    }

    suspend fun deleteHiddenNote(hiddenNote: HiddenNote) {
        noteDao.deleteHiddenNote(hiddenNote)
    }

    fun searchHiddenNotes(userId: String, query: String): Flow<List<HiddenNote>> {
        return noteDao.searchHiddenNotes(userId, query)
    }

    fun decryptHiddenNoteContent(hiddenNote: HiddenNote): String {
        return encryption.decrypt(hiddenNote.content)
    }
}

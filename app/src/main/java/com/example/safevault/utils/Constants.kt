package com.example.safevault.utils

object Constants {
    // Intent extras
    const val EXTRA_NOTE_ID = "extra_note_id"
    const val EXTRA_HIDDEN_NOTE_ID = "extra_hidden_note_id"
    const val EXTRA_IS_NEW_NOTE = "extra_is_new_note"
    
    // Request codes
    const val REQUEST_CODE_GOOGLE_SIGN_IN = 100
    const val REQUEST_CODE_FINGERPRINT = 101
    const val REQUEST_CODE_FACE_ID = 102
    const val REQUEST_CODE_PIN = 103
    
    // Shared preferences keys
    const val PREF_HIDDEN_NOTES_ENABLED = "pref_hidden_notes_enabled"
    const val PREF_HIDDEN_NOTES_KEYWORD = "pref_hidden_notes_keyword"
    
    // Authentication methods
    const val AUTH_METHOD_FINGERPRINT = "fingerprint"
    const val AUTH_METHOD_FACE_ID = "face_id"
    const val AUTH_METHOD_PIN = "pin"
    
    // Result codes
    const val RESULT_AUTH_SUCCESS = 1000
    const val RESULT_AUTH_FAILED = 1001
    
    // Default values
    const val DEFAULT_PIN_LENGTH = 6


}
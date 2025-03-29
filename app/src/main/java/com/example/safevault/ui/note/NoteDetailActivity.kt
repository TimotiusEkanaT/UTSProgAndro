package com.example.safevault.ui.note

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.safevault.R
import com.example.safevault.safevault.SafeVaultApplication
import com.example.safevault.data.repository.NoteRepository
import com.example.safevault.data.repository.UserRepository
import com.example.safevault.databinding.ActivityNoteDetailBinding
import com.example.safevault.utils.Constants
import com.example.safevault.utils.showToast

class NoteDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteDetailBinding
    private lateinit var viewModel: NoteDetailViewModel

    private var isNewNote = false
    private var noteId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        parseIntent()
        setupListeners()
        observeViewModel()

        if (!isNewNote) {
            viewModel.loadNote(noteId)
        }
    }

    private fun initViewModel() {
        val factory = NoteDetailViewModelFactory(
            (application as SafeVaultApplication).noteRepository,
            (application as SafeVaultApplication).userRepository
        )
        viewModel = ViewModelProvider(this, factory)[NoteDetailViewModel::class.java]
    }

    private fun parseIntent() {
        isNewNote = intent.getBooleanExtra(Constants.EXTRA_IS_NEW_NOTE, true)
        noteId = intent.getLongExtra(Constants.EXTRA_NOTE_ID, 0)
    }

    private fun setupListeners() {
        binding.ivSave.setOnClickListener {
            saveNote()
        }

        binding.ivDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun observeViewModel() {
        viewModel.note.observe(this) { note ->
            note?.let {
                binding.etTitle.setText(it.title)
                binding.etContent.setText(it.content)
            }
        }

        viewModel.savingStatus.observe(this) { status ->
            when (status) {
                is NoteDetailViewModel.SavingStatus.Success -> {
                    showToast("Note saved")
                    finish()
                }
                is NoteDetailViewModel.SavingStatus.Error -> {
                    showToast("Error: ${status.message}")
                }
            }
        }
    }

    private fun saveNote() {
        val title = binding.etTitle.text.toString()
        val content = binding.etContent.text.toString()

        if (title.isEmpty()) {
            showToast("Please enter a title")
            return
        }

        if (isNewNote) {
            viewModel.createNote(title, content)
        } else {
            viewModel.updateNote(noteId, title, content)
        }
    }

    private fun showDeleteConfirmationDialog() {
        if (isNewNote) {
            finish()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteNote(noteId)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_note_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_save -> {
                saveNote()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

class NoteDetailViewModelFactory(
    private val noteRepository: NoteRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteDetailViewModel(noteRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
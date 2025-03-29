package com.example.safevault.ui.hidden

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
import com.example.safevault.databinding.ActivityHiddenNoteDetailBinding
import com.example.safevault.utils.Constants
import com.example.safevault.utils.showToast

class HiddenNoteDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHiddenNoteDetailBinding
    private lateinit var viewModel: HiddenNoteDetailViewModel

    private var isNewNote = false
    private var noteId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHiddenNoteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        parseIntent()
        setupListeners()
        observeViewModel()

        if (!isNewNote) {
            viewModel.loadHiddenNote(noteId)
        }
    }

    private fun initViewModel() {
        val factory = HiddenNoteDetailViewModelFactory(
            (application as SafeVaultApplication).noteRepository,
            (application as SafeVaultApplication).userRepository
        )
        viewModel = ViewModelProvider(this, factory)[HiddenNoteDetailViewModel::class.java]
    }

    private fun parseIntent() {
        isNewNote = intent.getBooleanExtra(Constants.EXTRA_IS_NEW_NOTE, true)
        noteId = intent.getLongExtra(Constants.EXTRA_HIDDEN_NOTE_ID, 0)
    }

    private fun setupListeners() {
        binding.ivSave.setOnClickListener {
            saveHiddenNote()
        }

        binding.ivDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun observeViewModel() {
        viewModel.hiddenNote.observe(this) { hiddenNote ->
            hiddenNote?.let {
                binding.etTitle.setText(it.title)
                // Decrypt the content for display
                val decryptedContent = viewModel.getDecryptedContent(it)
                binding.etContent.setText(decryptedContent)
            }
        }

        viewModel.savingStatus.observe(this) { status ->
            when (status) {
                is HiddenNoteDetailViewModel.SavingStatus.Success -> {
                    showToast("Note saved")
                    finish()
                }
                is HiddenNoteDetailViewModel.SavingStatus.Error -> {
                    showToast("Error: ${status.message}")
                }
            }
        }
    }

    private fun saveHiddenNote() {
        val title = binding.etTitle.text.toString()
        val content = binding.etContent.text.toString()

        if (title.isEmpty()) {
            showToast("Please enter a title")
            return
        }

        if (isNewNote) {
            viewModel.createHiddenNote(title, content)
        } else {
            viewModel.updateHiddenNote(noteId, title, content)
        }
    }

    private fun showDeleteConfirmationDialog() {
        if (isNewNote) {
            finish()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Delete Hidden Note")
            .setMessage("Are you sure you want to delete this hidden note?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteHiddenNote(noteId)
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
                saveHiddenNote()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

class HiddenNoteDetailViewModelFactory(
    private val noteRepository: NoteRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HiddenNoteDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HiddenNoteDetailViewModel(noteRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
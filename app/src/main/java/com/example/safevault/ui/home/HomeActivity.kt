package com.example.safevault.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safevault.R
import com.example.safevault.safevault.SafeVaultApplication
import com.example.safevault.data.model.Note
import com.example.safevault.data.repository.NoteRepository
import com.example.safevault.data.repository.UserRepository
import com.example.safevault.databinding.ActivityHomeBinding
import com.example.safevault.ui.auth.LoginActivity
import com.example.safevault.ui.hidden.HiddenNoteActivity
import com.example.safevault.ui.hidden.HiddenSettingActivity
import com.example.safevault.ui.note.NoteDetailActivity
import com.example.safevault.utils.Constants

class HomeActivity : AppCompatActivity(), NoteAdapter.NoteClickListener {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun initViewModel() {
        val factory = HomeViewModelFactory(
            (application as SafeVaultApplication).noteRepository,
            (application as SafeVaultApplication).userRepository
        )
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(this)
        binding.recyclerNotes.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = noteAdapter
        }
    }

    private fun setupListeners() {
        binding.fabAddNote.setOnClickListener {
            val intent = Intent(this, NoteDetailActivity::class.java).apply {
                putExtra(Constants.EXTRA_IS_NEW_NOTE, true)
            }
            startActivity(intent)
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchQuery = text.toString()
                // Check if search query matches hidden note keyword
                if (viewModel.isHiddenNoteEnabled() && searchQuery == viewModel.getHiddenNoteKeyword()) {
                    // Open hidden notes screen
                    openHiddenNotes()
                    binding.etSearch.setText("") // Clear search field
                } else {
                    viewModel.search(searchQuery)
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.ivSettings.setOnClickListener {
            val intent = Intent(this, HiddenSettingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.notes.observe(this) { notes ->
            noteAdapter.submitList(notes)
            updateEmptyViewVisibility(notes.isEmpty())
        }

        viewModel.user.observe(this) { user ->
            // User data has been loaded
        }
    }

    private fun updateEmptyViewVisibility(isEmpty: Boolean) {
        binding.tvEmptyView.visibility = if (isEmpty) android.view.View.VISIBLE else android.view.View.GONE
    }

    override fun onNoteClick(note: Note) {
        val intent = Intent(this, NoteDetailActivity::class.java).apply {
            putExtra(Constants.EXTRA_NOTE_ID, note.id)
            putExtra(Constants.EXTRA_IS_NEW_NOTE, false)
        }
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                viewModel.signOut()
                navigateToLogin()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun openHiddenNotes() {
        val intent = Intent(this, HiddenNoteActivity::class.java)
        startActivity(intent)
    }
}

class HomeViewModelFactory(
    private val noteRepository: NoteRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(noteRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
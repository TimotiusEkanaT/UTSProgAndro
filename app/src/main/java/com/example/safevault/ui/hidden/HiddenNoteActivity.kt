package com.example.safevault.ui.hidden

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safevault.safevault.SafeVaultApplication
import com.example.safevault.data.model.HiddenNote
import com.example.safevault.databinding.ActivitySecreteVaultBinding
import com.example.safevault.utils.Constants
import com.example.safevault.utils.showToast

class HiddenNoteActivity : AppCompatActivity(), HiddenNoteAdapter.HiddenNoteClickListener {

    private lateinit var binding: ActivitySecreteVaultBinding
    private lateinit var viewModel: HiddenNoteViewModel
    private lateinit var hiddenNoteAdapter: HiddenNoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecreteVaultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        checkAuthStatus()
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun initViewModel() {
        val factory = HiddenNoteViewModelFactory(
            (application as SafeVaultApplication).noteRepository,
            (application as SafeVaultApplication).userRepository
        )
        viewModel = ViewModelProvider(this, factory)[HiddenNoteViewModel::class.java]
    }

    private fun checkAuthStatus() {
        viewModel.checkAuthStatus()
    }

    private fun setupRecyclerView() {
        hiddenNoteAdapter = HiddenNoteAdapter(this)
        binding.recyclerHiddenNotes.apply {
            layoutManager = LinearLayoutManager(this@HiddenNoteActivity)
            adapter = hiddenNoteAdapter
        }
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.search(text.toString())
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.ivAddNote.setOnClickListener {
            val intent = Intent(this, HiddenNoteDetailActivity::class.java).apply {
                putExtra(Constants.EXTRA_IS_NEW_NOTE, true)
            }
            startActivity(intent)
        }

        binding.btnAuthenticate.setOnClickListener {
            verifyAuthentication()
        }
    }

    private fun observeViewModel() {
        viewModel.hiddenNotes.observe(this) { notes ->
            hiddenNoteAdapter.submitList(notes)
            updateEmptyViewVisibility(notes.isEmpty())
        }

        viewModel.authStatus.observe(this) { status ->
            when (status) {
                HiddenNoteViewModel.AuthStatus.AUTHORIZED -> {
                    // Show content
                    binding.layoutContent.visibility = View.VISIBLE
                    binding.layoutAuthRequired.visibility = View.GONE
                }
                HiddenNoteViewModel.AuthStatus.UNAUTHORIZED -> {
                    // Show authentication screen
                    verifyAuthentication()
                    binding.layoutContent.visibility = View.GONE
                    binding.layoutAuthRequired.visibility = View.VISIBLE
                }
                HiddenNoteViewModel.AuthStatus.NEEDS_SETUP -> {
                    // User needs to set up authentication first
                    showToast("Please set up authentication first")
                    navigateToSetupScreen()
                }
            }
        }
    }

    private fun updateEmptyViewVisibility(isEmpty: Boolean) {
        binding.tvEmptyView.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun verifyAuthentication() {
        val user = viewModel.user.value
        if (user == null) {
            showToast("User data not available")
            return
        }

        // Check what authentication methods are available and prompt accordingly
        when {
            user.hasFingerprintData -> {
                showBiometricPrompt(Constants.AUTH_METHOD_FINGERPRINT)
            }
            user.hasFaceIdData -> {
                showBiometricPrompt(Constants.AUTH_METHOD_FACE_ID)
            }
            user.hasPin -> {
                showPinPrompt()
            }
            else -> {
                showToast("No authentication method set up")
                navigateToSetupScreen()
            }
        }
    }

    private fun showBiometricPrompt(authMethod: String) {
        val intent = when (authMethod) {
            Constants.AUTH_METHOD_FINGERPRINT -> Intent(this, HiddenNoteVerifyFingerActivity::class.java)
            Constants.AUTH_METHOD_FACE_ID -> Intent(this, HiddenNoteVerifyFaceIDActivity::class.java)
            else -> return
        }

        startActivityForResult(intent, Constants.REQUEST_CODE_FINGERPRINT)
    }

    private fun showPinPrompt() {
        val intent = Intent(this, HiddenNoteVerifyPinActivity::class.java)
        startActivityForResult(intent, Constants.REQUEST_CODE_PIN)
    }

    private fun navigateToSetupScreen() {
        val intent = Intent(this, HiddenNoteActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Constants.RESULT_AUTH_SUCCESS) {
            viewModel.authorize()
        } else if (resultCode == Constants.RESULT_AUTH_FAILED) {
            showToast("Authentication failed")
            finish()
        }
    }

    override fun onHiddenNoteClick(hiddenNote: HiddenNote) {
        val intent = Intent(this, HiddenNoteDetailActivity::class.java).apply {
            putExtra(Constants.EXTRA_HIDDEN_NOTE_ID, hiddenNote.id)
            putExtra(Constants.EXTRA_IS_NEW_NOTE, false)
        }
        startActivity(intent)
    }
}
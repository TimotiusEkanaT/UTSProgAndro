package com.example.believe

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Inisialisasi Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Inisialisasi input dan tombol
        val etEmail = findViewById<EditText>(R.id.et_email_signin)
        val etPassword = findViewById<EditText>(R.id.et_password_signin)
        val btnRegister = findViewById<LinearLayout>(R.id.btn_register)
        val btnBackToLogin = findViewById<LinearLayout>(R.id.btn_back_to_login)

        // Ketika tombol Register diklik
        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (!isValidEmail(email)) {
                Toast.makeText(this, "Format email tidak valid!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(this, "Password minimal 6 karakter!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Register user dengan Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Registrasi Gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        // Ketika tombol kembali ke login diklik
        btnBackToLogin.setOnClickListener {
            startActivity(Intent(this@SignInActivity, MainActivity::class.java))
            finish()
        }
    }

    // Fungsi untuk validasi email
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

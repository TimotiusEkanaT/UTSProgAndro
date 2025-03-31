package com.example.believe
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast




class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        Toast.makeText(this, "Selamat datang di Home Page!", Toast.LENGTH_LONG).show()
    }
}
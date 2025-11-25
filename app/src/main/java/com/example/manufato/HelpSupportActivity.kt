package com.example.manufato

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HelpSupportActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var emailContainer1: LinearLayout
    private lateinit var emailContainer2: LinearLayout
    private lateinit var emailText1: TextView
    private lateinit var emailText2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_support)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        emailContainer1 = findViewById(R.id.emailContainer1)
        emailContainer2 = findViewById(R.id.emailContainer2)
        emailText1 = findViewById(R.id.emailText1)
        emailText2 = findViewById(R.id.emailText2)
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            finish()
        }

        emailContainer1.setOnClickListener {
            sendEmail(emailText1.text.toString())
        }

        emailContainer2.setOnClickListener {
            sendEmail(emailText2.text.toString())
        }
    }

    private fun sendEmail(emailAddress: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // only email apps should handle this
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
            putExtra(Intent.EXTRA_SUBJECT, "Suporte Manufato")
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Nenhum aplicativo de e-mail encontrado", Toast.LENGTH_SHORT).show()
        }
    }
}

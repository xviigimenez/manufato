package com.example.manufato

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView
    private lateinit var forgotPassword: TextView
    private lateinit var userPrefs: UserPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize UserPreferences
        userPrefs = UserPreferences(this)
        
        // Check if user is already logged in
        if (userPrefs.isLoggedIn()) {
            navigateToMain()
            return
        }
        
        setContentView(R.layout.activity_login)
        
        initViews()
        setupListeners()
    }
    
    private fun initViews() {
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        registerLink = findViewById(R.id.registerLink)
        forgotPassword = findViewById(R.id.forgotPassword)
    }
    
    private fun setupListeners() {
        loginButton.setOnClickListener {
            performLogin()
        }
        
        registerLink.setOnClickListener {
            navigateToRegister()
        }
        
        forgotPassword.setOnClickListener {
            Toast.makeText(this, "Recuperação de senha em desenvolvimento", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun performLogin() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()
        
        // Validation
        if (email.isEmpty()) {
            emailInput.error = "Email é obrigatório"
            emailInput.requestFocus()
            return
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.error = "Email inválido"
            emailInput.requestFocus()
            return
        }
        
        if (password.isEmpty()) {
            passwordInput.error = "Senha é obrigatória"
            passwordInput.requestFocus()
            return
        }
        
        if (password.length < 6) {
            passwordInput.error = "Senha deve ter no mínimo 6 caracteres"
            passwordInput.requestFocus()
            return
        }
        
        // Check if user is registered
        if (!userPrefs.isUserRegistered()) {
            showToast("Usuário não encontrado. Por favor, cadastre-se primeiro.")
            return
        }
        
        // Attempt login
        if (userPrefs.login(email, password)) {
            showToast("Login realizado com sucesso!")
            navigateToMain()
        } else {
            showToast("Email ou senha incorretos")
            passwordInput.error = "Credenciais inválidas"
            passwordInput.requestFocus()
        }
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

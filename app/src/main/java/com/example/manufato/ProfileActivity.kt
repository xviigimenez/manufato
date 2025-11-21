package com.example.manufato

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {
    
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var editProfileItem: LinearLayout
    private lateinit var myOrdersItem: LinearLayout
    private lateinit var myProductsItem: LinearLayout
    private lateinit var favoritesItem: LinearLayout
    private lateinit var notificationsItem: LinearLayout
    private lateinit var settingsItem: LinearLayout
    private lateinit var helpItem: LinearLayout
    private lateinit var logoutButton: Button
    private lateinit var userPrefs: UserPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        
        // Initialize UserPreferences
        userPrefs = UserPreferences(this)
        
        initViews()
        setupListeners()
        loadUserData()
    }
    
    private fun initViews() {
        userName = findViewById(R.id.userName)
        userEmail = findViewById(R.id.userEmail)
        editProfileItem = findViewById(R.id.editProfileItem)
        myOrdersItem = findViewById(R.id.myOrdersItem)
        myProductsItem = findViewById(R.id.myProductsItem)
        favoritesItem = findViewById(R.id.favoritesItem)
        notificationsItem = findViewById(R.id.notificationsItem)
        settingsItem = findViewById(R.id.settingsItem)
        helpItem = findViewById(R.id.helpItem)
        logoutButton = findViewById(R.id.logoutButton)
    }
    
    private fun setupListeners() {
        editProfileItem.setOnClickListener {
            showToast("Editar perfil em desenvolvimento")
        }
        
        myOrdersItem.setOnClickListener {
            showToast("Meus pedidos em desenvolvimento")
        }
        
        myProductsItem.setOnClickListener {
            showToast("Meus produtos em desenvolvimento")
        }
        
        favoritesItem.setOnClickListener {
            showToast("Favoritos em desenvolvimento")
        }
        
        notificationsItem.setOnClickListener {
            showToast("Notificações em desenvolvimento")
        }
        
        settingsItem.setOnClickListener {
            showToast("Configurações em desenvolvimento")
        }
        
        helpItem.setOnClickListener {
            showToast("Ajuda e suporte em desenvolvimento")
        }
        
        logoutButton.setOnClickListener {
            performLogout()
        }
    }
    
    private fun loadUserData() {
        // Load actual user data from SharedPreferences
        val name = userPrefs.getUserName() ?: "Nome do Usuário"
        val email = userPrefs.getUserEmail() ?: "usuario@email.com"
        
        userName.text = name
        userEmail.text = email
    }
    
    private fun performLogout() {
        // Clear user session
        userPrefs.logout()
        showToast("Até logo!")
        
        // Navigate back to login
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

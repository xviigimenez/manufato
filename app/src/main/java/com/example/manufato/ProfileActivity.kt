package com.example.manufato

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File

class ProfileActivity : AppCompatActivity() {
    
    private lateinit var profileImage: ImageView
    private lateinit var avatarIcon: TextView
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
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var userPrefs: UserPreferences
    
    private val editProfileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadUserData()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        
        // Initialize UserPreferences
        userPrefs = UserPreferences(this)
        
        initViews()
        setupListeners()
        setupBottomNavigation()
        loadUserData()
    }
    
    private fun initViews() {
        profileImage = findViewById(R.id.profileImage)
        avatarIcon = findViewById(R.id.avatarIcon)
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
        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_profile
    }
    
    private fun setupListeners() {
        editProfileItem.setOnClickListener {
            navigateToEditProfile()
        }
        
        myOrdersItem.setOnClickListener {
            showToast("Meus pedidos em desenvolvimento")
        }
        
        myProductsItem.setOnClickListener {
            navigateToProducts()
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
        
        // Load profile image
        userPrefs.getProfileImageUri()?.let { uriString ->
            try {
                val uri = Uri.parse(uriString)
                val file = File(uri.path ?: return@let)
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(file.path)
                    profileImage.setImageBitmap(bitmap)
                    profileImage.visibility = View.VISIBLE
                    avatarIcon.visibility = View.GONE
                } else {
                    profileImage.visibility = View.GONE
                    avatarIcon.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                e.printStackTrace()
                profileImage.visibility = View.GONE
                avatarIcon.visibility = View.VISIBLE
            }
        }
    }
    
    private fun navigateToEditProfile() {
        val intent = Intent(this, EditProfileActivity::class.java)
        editProfileLauncher.launch(intent)
    }
    
    private fun navigateToProducts() {
        val intent = Intent(this, ProductsActivity::class.java)
        startActivity(intent)
    }
    
    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    navigateToHome()
                    true
                }
                R.id.nav_products -> {
                    navigateToProducts()
                    true
                }
                R.id.nav_cart -> {
                    showToast("Carrinho em desenvolvimento")
                    true
                }
                R.id.nav_profile -> {
                    // Already on profile screen
                    true
                }
                else -> false
            }
        }
    }
    
    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
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

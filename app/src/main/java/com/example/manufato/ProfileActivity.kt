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
import com.google.firebase.auth.FirebaseAuth
import java.io.File

class ProfileActivity : BaseActivity() {
    
    private lateinit var profileImage: ImageView
    private lateinit var avatarIcon: TextView
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var editProfileItem: LinearLayout
    private lateinit var myOrdersItem: LinearLayout
    private lateinit var myProductsItem: LinearLayout
    private lateinit var favoritesItem: LinearLayout
    private lateinit var notificationsItem: LinearLayout
    private lateinit var helpItem: LinearLayout
    private lateinit var logoutButton: Button
    private lateinit var userPrefs: UserPreferences
    private lateinit var auth: FirebaseAuth
    
    override fun getCurrentNavItemId(): Int = R.id.nav_profile
    
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
        
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        
        // Initialize UserPreferences
        userPrefs = UserPreferences(this)
        
        initViews()
        setupListeners()
        loadUserData()
        setSelectedNavItem(R.id.nav_profile)
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
        helpItem = findViewById(R.id.helpItem)
        logoutButton = findViewById(R.id.logoutButton)
    }
    
    private fun setupListeners() {
        editProfileItem.setOnClickListener {
            navigateToEditProfile()
        }
        
        myOrdersItem.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }
        
        myProductsItem.setOnClickListener {
            NavigationHelper.navigateToProducts(this)
        }
        
        favoritesItem.setOnClickListener {
            NavigationHelper.navigateToCart(this)
        }
        
        notificationsItem.setOnClickListener {
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }
        
        helpItem.setOnClickListener {
            val intent = Intent(this, HelpSupportActivity::class.java)
            startActivity(intent)
        }
        
        logoutButton.setOnClickListener {
            performLogout()
        }
    }
    
    private fun loadUserData() {
        // Load user data from Firebase
        val user = auth.currentUser
        if (user != null) {
            val name = user.displayName ?: "Nome do Usuário"
            val email = user.email ?: "usuario@email.com"
            
            userName.text = name
            userEmail.text = email
        } else {
            // Fallback to UserPreferences if not logged in via Firebase (shouldn't happen normally)
            val name = userPrefs.getUserName() ?: "Nome do Usuário"
            val email = userPrefs.getUserEmail() ?: "usuario@email.com"
            
            userName.text = name
            userEmail.text = email
        }
        
        // Load profile image from local storage for now (since Firebase Storage isn't set up yet)
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

    private fun performLogout() {
        // Sign out from Firebase
        auth.signOut()
        
        // Clear user session in local preferences if needed (though Firebase handles auth state)
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

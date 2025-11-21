package com.example.manufato

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Handle window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        setupBottomNavigation()
        setupSearchBar()
        setupCategories()
    }
    
    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_home
        
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Already on home
                    true
                }
                R.id.nav_products -> {
                    showToast("Produtos em desenvolvimento")
                    false
                }
                R.id.nav_favorites -> {
                    showToast("Favoritos em desenvolvimento")
                    false
                }
                R.id.nav_profile -> {
                    navigateToProfile()
                    false
                }
                else -> false
            }
        }
    }
    
    private fun setupSearchBar() {
        val searchBar = findViewById<android.view.View>(R.id.searchBar)
        searchBar.setOnClickListener {
            showToast("Busca em desenvolvimento")
        }
    }
    
    private fun setupCategories() {
        // Categories are clickable but functionality is not implemented yet
        // This is a placeholder for future implementation
    }
    
    private fun navigateToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
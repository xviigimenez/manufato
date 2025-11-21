package com.example.manufato

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    
    private lateinit var userPrefs: UserPreferences
    private lateinit var productPrefs: ProductPreferences
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var productAdapter: HomeProductAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize UserPreferences
        userPrefs = UserPreferences(this)
        productPrefs = ProductPreferences(this)
        
        // Check if user is logged in
        if (!userPrefs.isLoggedIn()) {
            navigateToLogin()
            return
        }
        
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
        setupProductsList()
    }
    
    override fun onResume() {
        super.onResume()
        loadProducts()
    }
    
    private fun setupProductsList() {
        productsRecyclerView = findViewById(R.id.productsRecyclerView)
        productsRecyclerView.layoutManager = LinearLayoutManager(this)
        
        productAdapter = HomeProductAdapter(emptyList()) { product ->
            // TODO: Navigate to product detail
            showToast("Produto: ${product.name}")
        }
        
        productsRecyclerView.adapter = productAdapter
        loadProducts()
    }
    
    private fun loadProducts() {
        val products = productPrefs.getProducts()
        // Only show available products on home page
        val availableProducts = products.filter { it.isAvailable }
        productAdapter.updateProducts(availableProducts)
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
                    navigateToProducts()
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
    
    private fun navigateToProducts() {
        val intent = Intent(this, ProductsActivity::class.java)
        startActivity(intent)
    }
    
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
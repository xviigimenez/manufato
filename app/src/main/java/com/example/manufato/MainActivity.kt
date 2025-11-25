package com.example.manufato

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
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
    private lateinit var categorySpinner: Spinner
    
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
        setupCategorySpinner()
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
                R.id.nav_cart -> {
                    showToast("Carrinho em desenvolvimento")
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
    
    private fun setupCategorySpinner() {
        categorySpinner = findViewById(R.id.categorySpinner)
        
        val categories = listOf(
            getString(R.string.categories_title),
            getString(R.string.category_ceramics),
            getString(R.string.category_textiles),
            getString(R.string.category_jewelry),
            getString(R.string.category_wood),
            getString(R.string.category_leather),
            getString(R.string.category_art)
        )
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
        
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    val selectedCategory = categories[position]
                    showToast("Categoria selecionada: $selectedCategory")
                    // TODO: Filter products by category
                }
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
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

package com.example.manufato

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProductsActivity : AppCompatActivity() {
    
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var fabAddProduct: FrameLayout
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productPrefs: ProductPreferences
    
    private val addEditProductLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadProducts()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)
        
        productPrefs = ProductPreferences(this)
        
        initViews()
        setupRecyclerView()
        setupListeners()
        loadProducts()
    }
    
    override fun onResume() {
        super.onResume()
        loadProducts()
    }
    
    private fun initViews() {
        productsRecyclerView = findViewById(R.id.productsRecyclerView)
        emptyState = findViewById(R.id.emptyState)
        fabAddProduct = findViewById(R.id.fabAddProduct)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_products
    }
    
    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(emptyList()) { product ->
            editProduct(product)
        }
        
        productsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ProductsActivity)
            adapter = productAdapter
        }
    }
    
    private fun setupListeners() {
        fabAddProduct.setOnClickListener {
            addProduct()
        }
        
        setupBottomNavigation()
    }
    
    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    navigateToHome()
                    true
                }
                R.id.nav_products -> {
                    // Already on products screen
                    true
                }
                R.id.nav_favorites -> {
                    // TODO: Navigate to favorites
                    true
                }
                R.id.nav_profile -> {
                    navigateToProfile()
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
    
    private fun navigateToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        finish()
    }
    
    private fun loadProducts() {
        val products = productPrefs.getProducts()
        
        if (products.isEmpty()) {
            emptyState.visibility = View.VISIBLE
            productsRecyclerView.visibility = View.GONE
        } else {
            emptyState.visibility = View.GONE
            productsRecyclerView.visibility = View.VISIBLE
            productAdapter.updateProducts(products)
        }
    }
    
    private fun addProduct() {
        val intent = Intent(this, AddEditProductActivity::class.java)
        addEditProductLauncher.launch(intent)
    }
    
    private fun editProduct(product: Product) {
        val intent = Intent(this, AddEditProductActivity::class.java)
        intent.putExtra("product", product)
        addEditProductLauncher.launch(intent)
    }
}

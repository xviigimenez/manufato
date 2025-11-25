package com.example.manufato

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : BaseActivity() {
    
    private lateinit var searchBar: LinearLayout
    private lateinit var categorySpinner: Spinner
    private lateinit var productsRecyclerView: RecyclerView
    
    override fun getCurrentNavItemId(): Int = R.id.nav_home
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupCategorySpinner()
        setupProductsRecyclerView()
        setupSearchListener()
    }
    
    private fun initViews() {
        searchBar = findViewById(R.id.searchBar)
        categorySpinner = findViewById(R.id.categorySpinner)
        productsRecyclerView = findViewById(R.id.productsRecyclerView)
    }
    
    private fun setupCategorySpinner() {
        val categories = arrayOf(
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
    }
    
    private fun setupProductsRecyclerView() {
        // Setup RecyclerView with a GridLayoutManager (2 columns)
        productsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        
        // This is where we would set the adapter with actual product data
        // For now, the RecyclerView will be empty until we implement the adapter
    }
    
    private fun setupSearchListener() {
        searchBar.setOnClickListener {
            Toast.makeText(this, "Busca em desenvolvimento", Toast.LENGTH_SHORT).show()
        }
    }
}

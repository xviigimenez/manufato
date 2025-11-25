package com.example.manufato

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class CartActivity : AppCompatActivity() {

    private lateinit var emptyCartView: LinearLayout
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartSummaryContainer: CardView
    private lateinit var startShoppingButton: Button
    private lateinit var checkoutButton: Button
    private lateinit var totalAmount: TextView
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        initViews()
        setupListeners()
        setupBottomNavigation()
        loadCart()
    }

    private fun initViews() {
        emptyCartView = findViewById(R.id.emptyCartView)
        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        cartSummaryContainer = findViewById(R.id.cartSummaryContainer)
        startShoppingButton = findViewById(R.id.startShoppingButton)
        checkoutButton = findViewById(R.id.checkoutButton)
        totalAmount = findViewById(R.id.totalAmount)
        bottomNavigation = findViewById(R.id.bottomNavigation)
    }

    private fun setupListeners() {
        startShoppingButton.setOnClickListener {
            NavigationHelper.navigateToProducts(this)
        }

        checkoutButton.setOnClickListener {
            // TODO: Implement checkout logic
        }
    }

    private fun setupBottomNavigation() {
        NavigationHelper.setupBottomNavigation(this, bottomNavigation, R.id.nav_cart)
    }

    private fun loadCart() {
        // TODO: Load actual cart items from database
        // For now, we'll just show the empty state
        showEmptyCart()
    }

    private fun showEmptyCart() {
        emptyCartView.visibility = View.VISIBLE
        cartRecyclerView.visibility = View.GONE
        cartSummaryContainer.visibility = View.GONE
    }

    private fun showCartItems() {
        emptyCartView.visibility = View.GONE
        cartRecyclerView.visibility = View.VISIBLE
        cartSummaryContainer.visibility = View.VISIBLE
    }
}

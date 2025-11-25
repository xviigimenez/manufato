package com.example.manufato

import android.app.Activity
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView

object NavigationHelper {
    
    /**
     * Setup bottom navigation for any activity
     */
    fun setupBottomNavigation(
        activity: Activity,
        bottomNavigation: BottomNavigationView,
        currentNavItem: Int
    ) {
        bottomNavigation.selectedItemId = currentNavItem
        
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (item.itemId != currentNavItem) {
                        navigateToHome(activity)
                    }
                    true
                }
                R.id.nav_products -> {
                    if (item.itemId != currentNavItem) {
                        navigateToProducts(activity)
                    }
                    true
                }
                R.id.nav_cart -> {
                    if (item.itemId != currentNavItem) {
                        navigateToCart(activity)
                    }
                    true
                }
                R.id.nav_profile -> {
                    if (item.itemId != currentNavItem) {
                        navigateToProfile(activity)
                    }
                    true
                }
                else -> false
            }
        }
    }
    
    /**
     * Navigate to home screen
     */
    fun navigateToHome(activity: Activity) {
        if (activity is MainActivity) return
        
        val intent = Intent(activity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        activity.startActivity(intent)
        activity.overridePendingTransition(0, 0)
    }
    
    /**
     * Navigate to products screen
     */
    fun navigateToProducts(activity: Activity) {
        if (activity is ProductsActivity) return
        
        val intent = Intent(activity, ProductsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        activity.startActivity(intent)
        activity.overridePendingTransition(0, 0)
    }
    
    /**
     * Navigate to cart screen
     */
    fun navigateToCart(activity: Activity) {
        if (activity is CartActivity) return
        
        val intent = Intent(activity, CartActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        activity.startActivity(intent)
        activity.overridePendingTransition(0, 0)
    }
    
    /**
     * Navigate to profile screen
     */
    fun navigateToProfile(activity: Activity) {
        if (activity is ProfileActivity) return
        
        val intent = Intent(activity, ProfileActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        activity.startActivity(intent)
        activity.overridePendingTransition(0, 0)
    }
}

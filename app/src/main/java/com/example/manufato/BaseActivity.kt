package com.example.manufato

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class BaseActivity : AppCompatActivity() {
    
    private lateinit var bottomNavigation: BottomNavigationView
    
    /**
     * Override to set content view with bottom navigation wrapper
     */
    override fun setContentView(layoutResID: Int) {
        val container = layoutInflater.inflate(R.layout.activity_base, null) as ViewGroup
        val activityContainer = container.findViewById<FrameLayout>(R.id.activityContainer)
        layoutInflater.inflate(layoutResID, activityContainer, true)
        super.setContentView(container)
        
        bottomNavigation = findViewById(R.id.bottomNavigation)
        setupBottomNavigation()
    }
    
    /**
     * Setup bottom navigation with the current activity's nav item
     */
    private fun setupBottomNavigation() {
        val currentNavItem = getCurrentNavItemId()
        NavigationHelper.setupBottomNavigation(this, bottomNavigation, currentNavItem)
    }
    
    /**
     * Get the navigation item ID for the current activity
     * Override in subclasses to specify which nav item should be selected
     */
    protected abstract fun getCurrentNavItemId(): Int
    
    /**
     * Set the selected navigation item programmatically
     */
    protected fun setSelectedNavItem(itemId: Int) {
        bottomNavigation.selectedItemId = itemId
    }
}

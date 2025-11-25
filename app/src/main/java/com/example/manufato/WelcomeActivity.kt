package com.example.manufato

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class WelcomeActivity : AppCompatActivity() {
    
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var btnNext: Button
    private lateinit var btnSkip: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if user has seen welcome screen
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        if (prefs.getBoolean("welcome_shown", false)) {
            navigateToLogin()
            return
        }
        
        setContentView(R.layout.activity_welcome)
        
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        btnNext = findViewById(R.id.btnNext)
        btnSkip = findViewById(R.id.btnSkip)
        
        val welcomePages = listOf(
            WelcomePage(
                "Descubra Produtos Artesanais Únicos",
                "Navegue por uma coleção selecionada de artesanato de criadores talentosos de todo o mundo",
                R.drawable.ic_products
            ),
            WelcomePage(
                "Apoie Artesãos Locais",
                "Conecte-se diretamente com os criadores e apoie seu ofício. Cada compra faz a diferença",
                R.drawable.ic_favorite
            ),
            WelcomePage(
                "Comece Sua Jornada",
                "Crie sua conta e faça parte da nossa comunidade de artesãos e entusiastas do artesanato",
                R.drawable.ic_home
            )
        )
        
        viewPager.adapter = WelcomeAdapter(welcomePages)
        
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()
        
        btnNext.setOnClickListener {
            if (viewPager.currentItem < welcomePages.size - 1) {
                viewPager.currentItem += 1
            } else {
                finishWelcome()
            }
        }
        
        btnSkip.setOnClickListener {
            finishWelcome()
        }
        
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == welcomePages.size - 1) {
                    btnNext.text = "Começar"
                    btnSkip.visibility = View.GONE
                } else {
                    btnNext.text = "Próximo"
                    btnSkip.visibility = View.VISIBLE
                }
            }
        })
    }
    
    private fun finishWelcome() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        prefs.edit().putBoolean("welcome_shown", true).apply()
        navigateToLogin()
    }
    
    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}

data class WelcomePage(
    val title: String,
    val description: String,
    val imageRes: Int
)

class WelcomeAdapter(private val pages: List<WelcomePage>) : 
    RecyclerView.Adapter<WelcomeAdapter.WelcomeViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WelcomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_welcome_page, parent, false)
        return WelcomeViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: WelcomeViewHolder, position: Int) {
        holder.bind(pages[position])
    }
    
    override fun getItemCount() = pages.size
    
    class WelcomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.welcomeImage)
        private val titleView: TextView = itemView.findViewById(R.id.welcomeTitle)
        private val descriptionView: TextView = itemView.findViewById(R.id.welcomeDescription)
        
        fun bind(page: WelcomePage) {
            imageView.setImageResource(page.imageRes)
            titleView.text = page.title
            descriptionView.text = page.description
        }
    }
}

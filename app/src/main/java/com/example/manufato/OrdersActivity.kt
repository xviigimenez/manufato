package com.example.manufato

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OrdersActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        initViews()
        setupListeners()
        loadOrders()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        recyclerView = findViewById(R.id.ordersRecyclerView)
        emptyState = findViewById(R.id.emptyState)
        
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadOrders() {
        // Mock data for demonstration
        val orders = listOf(
            Order(
                id = "12345",
                date = "15/05/2024",
                status = "Entregue",
                total = 159.90,
                items = listOf(
                    OrderItem("Vaso de Cerâmica Artesanal", 1, 89.90),
                    OrderItem("Colar de Prata e Pedra", 1, 70.00)
                )
            ),
            Order(
                id = "12344",
                date = "02/05/2024",
                status = "Em trânsito",
                total = 45.00,
                items = listOf(
                    OrderItem("Sabonete Artesanal Lavanda", 3, 15.00)
                )
            ),
            Order(
                id = "12340",
                date = "20/04/2024",
                status = "Entregue",
                total = 120.00,
                items = listOf(
                    OrderItem("Carteira de Couro", 1, 120.00)
                )
            )
        )

        if (orders.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyState.visibility = View.GONE
            recyclerView.adapter = OrdersAdapter(orders)
        }
    }
}

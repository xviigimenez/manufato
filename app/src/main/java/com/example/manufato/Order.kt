package com.example.manufato

data class Order(
    val id: String,
    val date: String,
    val status: String, // "Entregue", "Em trânsito", "Pendente"
    val total: Double,
    val items: List<OrderItem>
)

data class OrderItem(
    val productName: String,
    val quantity: Int,
    val price: Double,
    val imageUrl: String? = null
)

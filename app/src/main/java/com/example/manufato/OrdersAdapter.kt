package com.example.manufato

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class OrdersAdapter(private val orders: List<Order>) : 
    RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderId: TextView = view.findViewById(R.id.orderId)
        val orderDate: TextView = view.findViewById(R.id.orderDate)
        val orderStatus: TextView = view.findViewById(R.id.orderStatus)
        val orderTotal: TextView = view.findViewById(R.id.orderTotal)
        val orderItemsList: RecyclerView = view.findViewById(R.id.orderItemsList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        val context = holder.itemView.context
        val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        holder.orderId.text = "Pedido #${order.id}"
        holder.orderDate.text = order.date
        holder.orderStatus.text = order.status
        holder.orderTotal.text = format.format(order.total)

        // Set status color
        val statusColor = when (order.status) {
            "Entregue" -> R.color.success_green
            "Em trânsito" -> R.color.primary
            "Cancelado" -> R.color.error_red
            else -> R.color.text_secondary
        }
        // holder.orderStatus.setTextColor(ContextCompat.getColor(context, statusColor))
        
        // Setup nested recycler view for items
        holder.orderItemsList.layoutManager = LinearLayoutManager(context)
        holder.orderItemsList.adapter = OrderItemsAdapter(order.items)
    }

    override fun getItemCount() = orders.size
}

class OrderItemsAdapter(private val items: List<OrderItem>) : 
    RecyclerView.Adapter<OrderItemsAdapter.OrderItemViewHolder>() {

    class OrderItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.itemName)
        val itemQuantity: TextView = view.findViewById(R.id.itemQuantity)
        val itemPrice: TextView = view.findViewById(R.id.itemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_product, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val item = items[position]
        val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        holder.itemName.text = item.productName
        holder.itemQuantity.text = "${item.quantity}x"
        holder.itemPrice.text = format.format(item.price)
    }

    override fun getItemCount() = items.size
}

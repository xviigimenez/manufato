package com.example.manufato

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotificationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.notifications)

        val recycler = findViewById<RecyclerView>(R.id.notificationsRecycler)
        recycler.layoutManager = LinearLayoutManager(this)
        val sample = listOf(
            NotificationItem(
                title = "Pedido confirmado",
                message = "Seu pedido #123 foi confirmado.",
                timestamp = "Agora",
                isUnread = true
            ),
            NotificationItem(
                title = "Novo seguidor",
                message = "@joao começou a seguir você.",
                timestamp = "1h"
            ),
            NotificationItem(
                title = "Atualização de produto",
                message = "Preço do produto 'Caneca' atualizado.",
                timestamp = "Ontem"
            )
        )
        recycler.adapter = NotificationsAdapter(sample)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

package com.example.manufato

data class NotificationItem(
    val title: String,
    val message: String,
    val timestamp: String,
    val isUnread: Boolean = false
)

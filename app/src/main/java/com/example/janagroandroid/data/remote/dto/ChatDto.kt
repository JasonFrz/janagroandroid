package com.example.janagroandroid.data.remote.dto

import java.util.Date

data class ChatMessageDto(
    val id: Long,
    val senderId: Long,
    val receiverId: Long,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

data class ChatRoomDto(
    val id: Long,
    val participantName: String,
    val lastMessage: String,
    val lastMessageTime: Long,
    val participantImageUrl: String? = null,
    val unreadCount: Int = 0
)
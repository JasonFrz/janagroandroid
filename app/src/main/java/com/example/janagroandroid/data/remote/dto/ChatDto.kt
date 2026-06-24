package com.example.janagroandroid.data.remote.dto

import com.squareup.moshi.Json

data class ChatMessageDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "sender_id") val senderId: Long,
    @Json(name = "receiver_id") val receiverId: Long,
    @Json(name = "message") val message: String,
    @Json(name = "is_read") val isRead: Boolean = false,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "status") val statusStr: String? = null,
    // Client-side mapping
    val status: MessageStatus = MessageStatus.SENT
) {
    fun getEffectiveStatus(): MessageStatus {
        return when (statusStr?.lowercase()) {
            "read" -> MessageStatus.READ
            "delivered" -> MessageStatus.DELIVERED
            "sent" -> MessageStatus.SENT
            else -> if (isRead) MessageStatus.READ else status
        }
    }
}

enum class MessageStatus {
    SENDING, SENT, DELIVERED, READ
}

data class ConversationResponse(
    val status: String? = null,
    val message: String? = null,
    val data: ConversationData? = null
)

data class ConversationData(
    val messages: List<ChatMessageDto> = emptyList()
)

data class ChatRoomDto(
    val id: Long,
    val participantName: String,
    val lastMessage: String,
    val lastMessageTime: Long,
    val participantImageUrl: String? = null,
    val unreadCount: Int = 0,
    val partnerId: Long
)

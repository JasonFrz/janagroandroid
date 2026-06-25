package com.example.janagroandroid.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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

@JsonClass(generateAdapter = true)
data class ConversationListResponse(
    val status: String? = null,
    val message: String? = null,
    val data: ConversationListData? = null
)

@JsonClass(generateAdapter = true)
data class ConversationListData(
    val conversations: List<ChatRoomResponse> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ChatRoomResponse(
    @Json(name = "partner_id") val partnerId: Long,
    @Json(name = "partner_name") val partnerName: String? = null,
    @Json(name = "partner_image") val partnerImage: String? = null,
    @Json(name = "last_message") val lastMessage: String? = null,
    @Json(name = "last_message_time") val lastMessageTime: Long = 0,
    @Json(name = "unread_count") val unreadCount: Int = 0
) {
    fun toRoom(): ChatRoomDto = ChatRoomDto(
        id = partnerId,
        participantName = partnerName ?: "Pengguna",
        lastMessage = lastMessage ?: "",
        lastMessageTime = lastMessageTime,
        participantImageUrl = partnerImage,
        unreadCount = unreadCount,
        partnerId = partnerId
    )
}

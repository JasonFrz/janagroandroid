package com.example.janagroandroid.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.local.SessionManager
import com.example.janagroandroid.data.remote.ApiService
import com.example.janagroandroid.data.remote.SocketManager
import com.example.janagroandroid.data.remote.dto.ChatMessageDto
import com.example.janagroandroid.data.remote.dto.MessageStatus
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class ChatViewModel(
    private val apiService: ApiService,
    private val socketManager: SocketManager,
    private val sessionManager: SessionManager,
    private val partnerId: Long
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessageDto>>(emptyList())
    val messages: StateFlow<List<ChatMessageDto>> = _messages.asStateFlow()

    private val _currentUserId = MutableStateFlow<Long>(-1)
    val currentUserId: StateFlow<Long> = _currentUserId.asStateFlow()

    init {
        _currentUserId.value = sessionManager.getUserId()
        fetchConversation()
        setupSocket()
    }

    private fun fetchConversation() {
        viewModelScope.launch {
            try {
                val response = apiService.getConversation(partnerId)
                if (response.isSuccessful) {
                    val history = response.body()?.data?.messages ?: emptyList()
                    val mappedHistory = history.map { 
                        it.copy(status = it.getEffectiveStatus())
                    }
                    _messages.value = mappedHistory
                    
                    // Mark incoming unread messages as read
                    history.filter { it.senderId == partnerId && !it.isRead }.forEach {
                        socketManager.markAsRead(it.id ?: 0L)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupSocket() {
        socketManager.connect()
        val socket = socketManager.getSocket()
        
        socketManager.joinConversation(partnerId)

        socket?.on("chat:message") { args ->
            val data = args.getOrNull(0) as? JSONObject ?: return@on
            val newMessage = ChatMessageDto(
                id = data.optLong("id"),
                senderId = data.optLong("sender_id"),
                receiverId = data.optLong("receiver_id"),
                message = data.optString("message"),
                isRead = data.optBoolean("is_read"),
                createdAt = data.optString("created_at"),
                statusStr = data.optString("status")
            ).let { it.copy(status = it.getEffectiveStatus()) }

            val current = _messages.value

            // Dedup: jika pesan dengan id ini sudah ada, abaikan
            if (newMessage.id != null && newMessage.id != 0L && current.any { it.id == newMessage.id }) {
                return@on
            }

            if (newMessage.senderId == _currentUserId.value) {
                // Echo pesan yang SAYA kirim: ganti pesan sementara (id null, teks sama)
                val tempIndex = current.indexOfFirst {
                    it.id == null && it.senderId == _currentUserId.value && it.message == newMessage.message
                }
                _messages.value = if (tempIndex != -1) {
                    current.toMutableList().also { it[tempIndex] = newMessage }
                } else {
                    current + newMessage
                }
            } else {
                // Pesan masuk dari lawan chat: tandai delivered + read (chat sedang terbuka)
                socketManager.markAsDelivered(newMessage.id ?: 0L)
                socketManager.markAsRead(newMessage.id ?: 0L)
                _messages.value = current + newMessage
            }
        }

        socket?.on("chat:status") { args ->
            val data = args.getOrNull(0) as? JSONObject ?: return@on
            val msgId = data.optLong("id")
            val statusStr = data.optString("status")
            
            _messages.value = _messages.value.map { 
                if (it.id == msgId) {
                    val newStatus = when (statusStr) {
                        "delivered" -> MessageStatus.DELIVERED
                        "read" -> MessageStatus.READ
                        else -> it.status
                    }
                    it.copy(status = newStatus)
                } else it
            }
        }
    }

    fun sendMessage(messageText: String) {
        if (messageText.isBlank()) return

        val tempMessage = ChatMessageDto(
            senderId = _currentUserId.value,
            receiverId = partnerId,
            message = messageText,
            status = MessageStatus.SENDING
        )
        
        _messages.value = _messages.value + tempMessage
        
        socketManager.sendMessage(partnerId, messageText)
    }

    override fun onCleared() {
        super.onCleared()
        socketManager.getSocket()?.off("chat:message")
        socketManager.getSocket()?.off("chat:status")
    }
}

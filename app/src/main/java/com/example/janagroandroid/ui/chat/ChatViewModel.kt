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
                    _messages.value = history.map { 
                        it.copy(status = if (it.isRead) MessageStatus.READ else MessageStatus.SENT)
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
                status = if (data.optBoolean("is_read")) MessageStatus.READ else MessageStatus.SENT
            )
            
            _messages.value = _messages.value + newMessage
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
    }
}

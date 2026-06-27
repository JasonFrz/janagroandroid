package com.example.janagroandroid.ui.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.remote.dto.ChatRoomDto
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatListViewModel(
    app: Application,
    private val repo: AppRepository
) : AndroidViewModel(app) {

    private val _conversations = MutableStateFlow<List<ChatRoomDto>>(emptyList())
    val conversations: StateFlow<List<ChatRoomDto>> = _conversations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun fetchConversations() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _conversations.value = repo.getConversations()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}

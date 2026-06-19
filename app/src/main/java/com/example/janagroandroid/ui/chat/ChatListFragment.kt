package com.example.janagroandroid.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.janagroandroid.data.remote.dto.ChatRoomDto
import com.example.janagroandroid.ui.theme.JanAgroTheme
import java.text.SimpleDateFormat
import java.util.*

class ChatListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                JanAgroTheme {
                    ChatListScreen(
                        onBackClick = { findNavController().navigateUp() },
                        onChatClick = { chatRoom ->
                            val action = ChatListFragmentDirections.actionChatListFragmentToChatFragment(
                                partnerId = chatRoom.partnerId,
                                partnerName = chatRoom.participantName
                            )
                            findNavController().navigate(action)
                        }
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ChatListScreen(
        onBackClick: () -> Unit,
        onChatClick: (ChatRoomDto) -> Unit
    ) {
        // Mock data
        val chatRooms = listOf(
            ChatRoomDto(1, "JanAgro Merchant", "Bisa kak, kalau order sebelum jam 3 sore.", System.currentTimeMillis(), unreadCount = 1, partnerId = 101L),
            ChatRoomDto(2, "Toko Berkah Tani", "Sama-sama kak, ditunggu orderannya.", System.currentTimeMillis() - 86400000, unreadCount = 0, partnerId = 102L),
            ChatRoomDto(3, "Pupuk Organik Jaya", "Baik kak, akan kami proses.", System.currentTimeMillis() - 172800000, unreadCount = 0, partnerId = 103L)
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Pesan", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            if (chatRooms.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Belum ada percakapan", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding)
                ) {
                    items(chatRooms) { room ->
                        ChatRoomItem(room = room, onClick = { onChatClick(room) })
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.LightGray)
                    }
                }
            }
        }
    }

    @Composable
    fun ChatRoomItem(room: ChatRoomDto, onClick: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder for Profile Image
            Surface(
                modifier = Modifier.size(50.dp).clip(CircleShape),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = room.participantName.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = room.participantName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = formatTime(room.lastMessageTime),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = room.lastMessage,
                        fontSize = 14.sp,
                        color = if (room.unreadCount > 0) Color.Black else Color.Gray,
                        fontWeight = if (room.unreadCount > 0) FontWeight.Medium else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (room.unreadCount > 0) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ) {
                            Text(room.unreadCount.toString())
                        }
                    }
                }
            }
        }
    }

    private fun formatTime(timestamp: Long): String {
        val now = Calendar.getInstance()
        val time = Calendar.getInstance().apply { timeInMillis = timestamp }
        
        return if (now.get(Calendar.DATE) == time.get(Calendar.DATE)) {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
        } else {
            SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date(timestamp))
        }
    }
}

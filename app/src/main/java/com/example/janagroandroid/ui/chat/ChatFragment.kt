package com.example.janagroandroid.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.janagroandroid.data.remote.dto.ChatMessageDto
import com.example.janagroandroid.ui.theme.JanAgroTheme
import java.text.SimpleDateFormat
import java.util.*

class ChatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                JanAgroTheme {
                    ChatScreen(
                        onBackClick = { findNavController().navigateUp() }
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ChatScreen(onBackClick: () -> Unit) {
        // Mock data for UI preview/testing
        val messages = remember {
            mutableStateListOf(
                ChatMessageDto(1, 1, 2, "Halo, apakah produk ini ready?", System.currentTimeMillis() - 3600000),
                ChatMessageDto(2, 2, 1, "Halo! Iya kak, produknya ready stok ya.", System.currentTimeMillis() - 3500000),
                ChatMessageDto(3, 1, 2, "Bisa kirim hari ini?", System.currentTimeMillis() - 3400000),
                ChatMessageDto(4, 2, 1, "Bisa kak, kalau order sebelum jam 3 sore.", System.currentTimeMillis() - 3300000)
            )
        }
        var inputText by remember { mutableStateOf("") }
        val currentUserId = 1L // Mock current user ID

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("JanAgro Merchant", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Online", fontSize = 12.sp, color = Color.Gray)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            bottomBar = {
                ChatInput(
                    text = inputText,
                    onTextChange = { inputText = it },
                    onSendClick = {
                        if (inputText.isNotBlank()) {
                            messages.add(
                                ChatMessageDto(
                                    id = messages.size.toLong() + 1,
                                    senderId = currentUserId,
                                    receiverId = 2,
                                    message = inputText
                                )
                            )
                            inputText = ""
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                reverseLayout = false,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(
                        message = message,
                        isMine = message.senderId == currentUserId
                    )
                }
            }
        }
    }

    @Composable
    fun MessageBubble(message: ChatMessageDto, isMine: Boolean) {
        val alignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart
        val bgColor = if (isMine) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0)
        val textColor = if (isMine) Color.White else Color.Black
        val shape = if (isMine) {
            RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
        } else {
            RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
        }

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
            Column(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(shape)
                    .background(bgColor)
                    .padding(12.dp)
            ) {
                Text(text = message.message, color = textColor, fontSize = 15.sp)
                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp)),
                    color = textColor.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }

    @Composable
    fun ChatInput(
        text: String,
        onTextChange: (String) -> Unit,
        onSendClick: () -> Unit
    ) {
        Surface(shadowElevation = 8.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Tulis pesan...") },
                    maxLines = 4,
                    shape = RoundedCornerShape(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onSendClick,
                    enabled = text.isNotBlank(),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}
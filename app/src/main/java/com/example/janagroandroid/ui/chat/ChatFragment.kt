package com.example.janagroandroid.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.janagroandroid.data.local.SessionManager
import com.example.janagroandroid.data.remote.RetrofitClient
import com.example.janagroandroid.data.remote.SocketManager
import com.example.janagroandroid.data.remote.dto.ChatMessageDto
import com.example.janagroandroid.data.remote.dto.MessageStatus
import com.example.janagroandroid.ui.AppViewModelFactory
import com.example.janagroandroid.ui.theme.JanAgroTheme
import java.text.SimpleDateFormat
import java.util.*

class ChatFragment : Fragment() {

    private val args: ChatFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                JanAgroTheme {
                    val context = LocalContext.current
                    val sessionManager = remember { SessionManager(context) }
                    val socketManager = remember { SocketManager(sessionManager) }
                    val viewModel: ChatViewModel = viewModel(
                        factory = AppViewModelFactory(
                            apiService = RetrofitClient.getApiService(sessionManager),
                            socketManager = socketManager,
                            sessionManager = sessionManager,
                            partnerId = args.partnerId
                        )
                    )

                    ChatScreen(
                        viewModel = viewModel,
                        partnerName = args.partnerName,
                        onBackClick = { findNavController().navigateUp() }
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ChatScreen(
        viewModel: ChatViewModel,
        partnerName: String,
        onBackClick: () -> Unit
    ) {
        val messages by viewModel.messages.collectAsState()
        val currentUserId by viewModel.currentUserId.collectAsState()
        var inputText by remember { mutableStateOf("") }
        val listState = rememberLazyListState()
        
        var showCounterNegoDialog by remember { mutableStateOf(false) }
        var counterNegoPriceInput by remember { mutableStateOf("") }
        var counteringMessageId by remember { mutableStateOf<Long?>(null) }
        var counteringProductId by remember { mutableStateOf<Long?>(null) }

        // Auto scroll to bottom when new message arrives
        LaunchedEffect(messages.size) {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
        
        var negoTriggered by remember { mutableStateOf(false) }
        val negoProductId = args.negoProductId
        val negoPrice = args.negoPrice

        LaunchedEffect(negoProductId, negoPrice, negoTriggered) {
            if (!negoTriggered && negoProductId != -1L && negoPrice != null) {
                negoTriggered = true
                viewModel.sendMessage(
                    messageText = "Saya mengajukan nego Rp $negoPrice",
                    type = "negotiation",
                    productId = negoProductId,
                    negotiatedPrice = negoPrice
                )
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(partnerName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(messages) { message ->
                        MessageBubble(
                            message = message,
                            isMine = message.senderId == currentUserId,
                            onRespondNegotiation = { status ->
                                if (message.id != null) {
                                    viewModel.respondNegotiation(message.id, status)
                                }
                            },
                            onCounterNegotiation = {
                                if (message.id != null) {
                                    counteringMessageId = message.id
                                    counteringProductId = message.productId
                                    counterNegoPriceInput = ""
                                    showCounterNegoDialog = true
                                }
                            }
                        )
                    }
                }
                
                if (showCounterNegoDialog) {
                    AlertDialog(
                        onDismissRequest = { showCounterNegoDialog = false },
                        title = { Text("Tawar Harga Balik", fontWeight = FontWeight.Bold) },
                        text = {
                            Column {
                                Text("Masukkan nominal harga baru (Rp):", fontSize = 14.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = counterNegoPriceInput,
                                    onValueChange = { if (it.all { char -> char.isDigit() }) counterNegoPriceInput = it },
                                    label = { Text("Harga Nego Baru") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    if (counterNegoPriceInput.isNotBlank()) {
                                        counteringMessageId?.let { msgId ->
                                            viewModel.respondNegotiation(msgId, "countered")
                                        }
                                        viewModel.sendMessage(
                                            messageText = "Saya mengajukan nego ulang Rp $counterNegoPriceInput",
                                            type = "negotiation",
                                            productId = counteringProductId,
                                            negotiatedPrice = counterNegoPriceInput
                                        )
                                        showCounterNegoDialog = false
                                        counterNegoPriceInput = ""
                                    }
                                }
                            ) {
                                Text("Ajukan")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showCounterNegoDialog = false }) {
                                Text("Batal", color = Color.Gray)
                            }
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun MessageBubble(
        message: ChatMessageDto,
        isMine: Boolean,
        onRespondNegotiation: (String) -> Unit,
        onCounterNegotiation: () -> Unit
    ) {
        val alignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart
        val bgColor = if (isMine) MaterialTheme.colorScheme.primary else Color(0xFFF0F0F0)
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
                if (message.type == "negotiation") {
                    Text(
                        text = "Pengajuan Negosiasi",
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = message.message, color = textColor, fontSize = 15.sp)
                    
                    if (message.negotiatedPrice != null) {
                        Text(
                            text = "Harga: Rp ${message.negotiatedPrice}",
                            color = textColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    when (message.negotiationStatus) {
                        "pending" -> {
                            if (!isMine) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Button(
                                        onClick = { onRespondNegotiation("rejected") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.weight(1f).padding(end = 4.dp)
                                    ) {
                                        Text("Tolak", fontSize = 12.sp, color = Color.White)
                                    }
                                    Button(
                                        onClick = { onRespondNegotiation("approved") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.weight(1f).padding(start = 4.dp)
                                    ) {
                                        Text("Terima", fontSize = 12.sp, color = Color.White)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(
                                    onClick = onCounterNegotiation,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Tawar Balik", fontSize = 12.sp, color = Color.White)
                                }
                            } else {
                                Text("Menunggu persetujuan...", color = textColor.copy(alpha = 0.8f), fontSize = 12.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                            }
                        }
                        "approved" -> {
                            Text("✅ Disetujui", color = if(isMine) Color.White else Color(0xFF4CAF50), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        "rejected" -> {
                            Text("❌ Ditolak", color = if(isMine) Color.White else Color.Red, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            if (isMine) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(
                                    onClick = onCounterNegotiation,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Ajukan Ulang", fontSize = 12.sp, color = Color.White)
                                }
                            }
                        }
                        "countered" -> {
                            Text("🔄 Nego Dibalas", color = if(isMine) Color.White else Color(0xFFFFB300), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                } else {
                    Text(text = message.message, color = textColor, fontSize = 15.sp)
                }
                
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val time = if (message.createdAt != null) {
                         // Simple ISO parsing or assume formatted from server
                         message.createdAt.substringAfter("T").substringBeforeLast(":")
                    } else {
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                    }
                    
                    Text(
                        text = time,
                        color = textColor.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                    
                    if (isMine) {
                        Spacer(modifier = Modifier.width(4.dp))
                        MessageStatusIcon(status = message.status)
                    }
                }
            }
        }
    }

    @Composable
    fun MessageStatusIcon(status: MessageStatus) {
        when (status) {
            MessageStatus.SENDING -> Icon(
                Icons.Default.Schedule,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = Color.White.copy(alpha = 0.7f)
            )
            MessageStatus.SENT -> Icon(
                Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = Color.White.copy(alpha = 0.7f)
            )
            MessageStatus.DELIVERED -> Icon(
                Icons.Default.DoneAll,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = Color.White.copy(alpha = 0.7f)
            )
            MessageStatus.READ -> Icon(
                Icons.Default.DoneAll,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = Color(0xFF00B0FF) // WhatsApp Blue
            )
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
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}
